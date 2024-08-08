package gdsc.cau.puangbe.photorequest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.exception.PhotoRequestException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.dto.ImageInfo;
import gdsc.cau.puangbe.user.entity.User;
import gdsc.cau.puangbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoRequestServiceImpl implements PhotoRequestService {

    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final ObjectMapper mapper;

    //이미지 처리 요청 생성 (RabbitMQ호출)
    @Override
    @Transactional
    public void createImage(CreateImageDto dto, Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND));
        // PhotoRequest 생성
        PhotoRequest request = PhotoRequest.builder()
                .user(user)
                .gender(Gender.fromInt(dto.getGender()))
                .urls(dto.getPhotoOriginUrls())
                .email(dto.getEmail())
                .build();
        photoRequestRepository.save(request);

        // PhotoRequest에 일대일 대응되는 PhotoResult 생성
        PhotoResult photoResult = PhotoResult.builder()
                .user(request.getUser())
                .photoRequest(request)
                .createDate(LocalDateTime.now())
                .build();
        photoResultRepository.save(photoResult);

        try {
            ImageInfo imageInfo = ImageInfo.builder()
                    .photoOriginUrls(dto.getPhotoOriginUrls())
                    .gender(Gender.fromInt(dto.getGender()))
                    .requestId(request.getId())
                    .build();
            String message = mapper.writeValueAsString(imageInfo);

            // TODO: RabbitMQ 호출
            // 1. RabbitMQ를 호출해서 message를 큐에 함께 넣어서 파이썬에서 접근할 수 있도록 한다.
            // 2. Redis에 <String keyName, Long requestId> 형식으로 진행되고 있는 request 정보를 저장한다.
            // 3. 추후 사진이 완성된다면 requestId를 통해 request를 찾아서 상태를 바꾸고 1:1 관계인 result에 접근해서 imageUrl를 수정한다.
            // 4. 즉, 파이썬에서 스프링으로 향하는 POST API는 {requestId, imageUrl}이 필수적으로 존재해야 한다.
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패");
            throw new PhotoRequestException(ResponseCode.JSON_PARSE_ERROR);
        }

        // Redis에 userId 저장하고, userId로 requestId 추적할 수 있도록 함
        redisTemplate.opsForSet().add(ConstantUtil.USER_ID_KEY, userId);
        redisTemplate.opsForSet().add(userId.toString(), request.getId());
    }

    // 유저의 전체 사진 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<String> getRequestImages(Long userId){
        validateUser(userId);

        // 현재 처리가 완료되지 않은 이미지(imageUrl이 null)는 보내지 않음
        return photoResultRepository.findAllByUserId(userId)
                .stream()
                .map(PhotoResult::getImageUrl)
                .filter(Objects::nonNull)
                .toList();
    }

    //최근 생성 요청한 이미지의 상태 조회 (추후 boolean 등으로 변환될 수도 있음)
    @Override
    @Transactional(readOnly = true)
    public String getRequestStatus(Long userId){
        validateUser(userId);

        // Redis에 userId가 존재하면 아직 처리 대기 중인 요청이므로 WAITING 반환
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ConstantUtil.USER_ID_KEY, userId))){
            return RequestStatus.WAITING.name();
        }

        RequestStatus status = photoRequestRepository.findTopByUserIdOrderByCreateDateDesc(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_REQUEST_NOT_FOUND))
                .getStatus();
        return status.name();
    }

    // 유저id 유효성 검사
    private void validateUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new BaseException(ResponseCode.USER_NOT_FOUND);
        }
    }
}
