package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
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
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    // 완성된 요청 id 및 imageUrl을 받아 저장
    @Override
    @Transactional
    public void uploadPhoto(Long photoRequestId, String imageUrl) {
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

        // Redis 대기열의 user 정보 삭제
        redisTemplate.opsForSet().remove(ConstantUtil.USER_ID_KEY, user.getId());
        redisTemplate.delete(user.getId().toString());

        // 이메일 발송
        sendEmail(photoRequest.getEmail(), imageUrl);
    }

    // 특정 요청의 imageUrl 조회
    @Override
    @Transactional(readOnly = true)
    public String getPhotoUrl(Long photoRequestId) {
        PhotoResult photoResult = getPhotoResult(photoRequestId);
        if(photoResult.getImageUrl() == null){
            throw new BaseException(ResponseCode.IMAGE_ON_PROCESS);
        }

        return photoResult.getImageUrl();
    }

    private void sendEmail(String email, String imageUrl) {
        // TODO : 이메일 발송 로직
    }

    private PhotoResult getPhotoResult(Long photoRequestId){
        return photoResultRepository.findByPhotoRequestId(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND));
    }
}
