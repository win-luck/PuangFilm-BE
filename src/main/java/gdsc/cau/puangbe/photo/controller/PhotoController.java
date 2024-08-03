package gdsc.cau.puangbe.photo.controller;

import gdsc.cau.puangbe.common.util.ApiResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/photo")
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping("/request")
    public ApiResponse<Long> createUploadRequest(Long photoRequestId) {
        return ApiResponse.success(photoService.createPhoto(photoRequestId), ResponseCode.PHOTORESULT_CREATE_SUCCESS.getMessage());
    }

    @PostMapping("/url")
    public ApiResponse<Void> uploadImage(Long photoResultId, String imageUrl) {
        return ApiResponse.success(photoService.uploadPhoto(photoResultId, imageUrl), ResponseCode.PHOTORESULT_URL_UPLOADED.getMessage());
    }

    // TODO : 이메일 발송 관련 api 추가

    @GetMapping("/{imageId}")
    public ApiResponse<String> getImage(Long photoRequestId) {
        return ApiResponse.success(photoService.getPhotoUrl(photoRequestId), ResponseCode.PHOTORESULT_URL_FOUND.getMessage());
    }

}