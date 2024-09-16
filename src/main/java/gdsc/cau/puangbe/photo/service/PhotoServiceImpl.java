package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.EmailInfoDto;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl{

    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    public EmailInfoDto uploadPhoto(Long photoRequestId, String imageUrl) {
        PhotoRequest photoRequest = photoRequestRepository.findById(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_REQUEST_NOT_FOUND));
        if (photoRequest.getStatus() == RequestStatus.FINISHED) {
            throw new BaseException(ResponseCode.URL_ALREADY_UPLOADED);
        }
        User user = photoRequest.getUser();
        PhotoResult photoResult = getPhotoResult(photoRequestId);

        photoRequest.finishStatus();
        photoRequestRepository.save(photoRequest);
        photoResult.update(imageUrl);
        photoResultRepository.save(photoResult);
        log.info("결과 이미지 URL 업로드 완료: {}", imageUrl);

        // Redis 대기열의 user 정보 삭제
        redisTemplate.opsForSet().remove(ConstantUtil.USER_ID_KEY, user.getId());
        redisTemplate.delete(user.getId().toString());
        log.info("Redis 대기열에서 요청 삭제 : {}", user.getId());

        // 이메일 DTO 만들어서 반환
        return EmailInfoDto.builder()
                .email(photoRequest.getEmail())
                .photoUrl(imageUrl)
                .name(user.getUserName())
                .build();
    }

    public PhotoResult getPhotoResult(Long photoRequestId){
        return photoResultRepository.findByPhotoRequestId(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND));
    }
}
