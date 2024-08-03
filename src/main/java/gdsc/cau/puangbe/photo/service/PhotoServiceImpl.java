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

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;

    @Override
    @Transactional
    public Long createPhoto(Long photoRequestId) {
        Optional<PhotoRequest> photoRequest = photoRequestRepository.findById(photoRequestId);

        if(!photoRequest.isPresent()){
            throw new BaseException(ResponseCode.BAD_REQUEST);
        }
        PhotoResult photoResult = PhotoResult.builder()
                .user(photoRequest.get().getUser())
                .photoRequest(photoRequest.get())
                .createDate(LocalDateTime.now())
                .build();

        // TODO: api호출 주체에서 photoRequest의 photoResult 부분도 업데이트

        return photoResultRepository.save(photoResult).getId();
    }

    @Override
    @Transactional
    public Void uploadPhoto(Long photoResultId,String imageUrl) {
        Optional<PhotoResult> photoResult = photoResultRepository.findById(photoResultId);
        if(photoResult.isPresent()){
            Optional<PhotoRequest> photoRequest = photoRequestRepository.findById(photoResult.get().getPhotoRequest().getId());
            if(photoRequest.isPresent()){
                if (photoRequest.get().getStatus() == RequestStatus.FINISHED) {
                    throw new BaseException(ResponseCode.URL_ALREADY_UPLOADED);
                }
            }
        } else {
            throw new BaseException(ResponseCode.PHOTORESULT_NOT_FOUND);
        }

        photoResult.get().update(imageUrl);

        // TODO : url 업로드 하고 PhotoRequest의 status 업데이트 (어느 메서드에서 할지 논의)

        return null;
    }

    public String get(Long photoRequestId) {
        Optional<PhotoResult> photoResult = photoResultRepository.findByPhotoRequestId(photoRequestId);
        if(!photoResult.isPresent()){
            throw new BaseException(ResponseCode.PHOTORESULT_NOT_FOUND);
        }
        return photoResult.get().getImageUrl();
    }

}
