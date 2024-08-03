package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoRepository;
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
    private final PhotoRepository photoRepository;
    private final PhotoRequestRepository photoRequestRepository;

    @Override
    @Transactional
    public Long createPhoto(Long photoRequestId) {
        Optional<PhotoRequest> photoRequest = photoRequestRepository.findById(photoRequestId);

        if(photoRequest.isPresent()){
            PhotoResult photoResult = PhotoResult.builder()
                    .user(photoRequest.get().getUser())
                    .photoRequest(photoRequest.get())
                    .createDate(LocalDateTime.now())
                    .build();

            // TODO: photoRequest도 업데이트 (비지니스 로직 builder 패턴으로 할지 상의 후 추가 작성 - 다른 위치에)

           return photoRepository.save(photoResult).getId();
        } else {
            throw new BaseException(ResponseCode.BAD_REQUEST);
        }
    }
}
