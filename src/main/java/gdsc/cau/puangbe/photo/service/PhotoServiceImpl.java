package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;

    @Override
    @Transactional
    public Long createPhoto(Long photoRequestId) {
        PhotoRequest photoRequest = photoRequestRepository.findById(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST));

        PhotoResult photoResult = PhotoResult.builder()
                .user(photoRequest.getUser())
                .photoRequest(photoRequest)
                .createDate(LocalDateTime.now())
                .build();

        // TODO: api호출 주체에서 photoRequest의 photoResult 부분도 업데이트

        return photoResultRepository.save(photoResult).getId();
    }

    @Override
    @Transactional
    public Void uploadPhoto(Long photoResultId,String imageUrl) {
        PhotoResult photoResult = photoResultRepository.findById(photoResultId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTORESULT_NOT_FOUND));

        PhotoRequest photoRequest = photoRequestRepository.findById(photoResult.getPhotoRequest().getId())
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTOREQUEST_NOT_FOUND));

        if (photoRequest.getStatus() == RequestStatus.FINISHED) {
            throw new BaseException(ResponseCode.URL_ALREADY_UPLOADED);
        }

        photoResult.update(imageUrl);
        photoResultRepository.save(photoResult);

        // TODO : url 업로드 하고 PhotoRequest의 status 업데이트 (어느 메서드에서 할지 논의)

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String getPhotoUrl(Long photoRequestId) {
        PhotoResult photoResult = photoResultRepository.findByPhotoRequestId(photoRequestId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTORESULT_NOT_FOUND));
        return photoResult.getImageUrl();
    }

}
