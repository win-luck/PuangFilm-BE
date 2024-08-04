package gdsc.cau.puangbe.photo.controller;

import gdsc.cau.puangbe.common.util.ApiResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.UploadImageDto;
import gdsc.cau.puangbe.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/photo")
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping("/{photoRequestId}")
    public ApiResponse<Long> createUploadRequest(@PathVariable Long photoRequestId) {
        return ApiResponse.success(photoService.createPhoto(photoRequestId), ResponseCode.PHOTO_RESULT_CREATE_SUCCESS.getMessage());
    }

    @PostMapping("/url")
    public ApiResponse<Void> uploadImage(@RequestBody UploadImageDto uploadImageDto) {
        return ApiResponse.success(photoService.uploadPhoto(uploadImageDto.getPhotoResultId(), uploadImageDto.getImageUrl()), ResponseCode.PHOTO_RESULT_URL_UPLOADED.getMessage());
    }

    // TODO : 이메일 발송 관련 api 추가

    @GetMapping("/{imageId}")
    public ApiResponse<String> getImage(@PathVariable("imageId") Long photoRequestId) {
        return ApiResponse.success(photoService.getPhotoUrl(photoRequestId), ResponseCode.PHOTO_RESULT_URL_FOUND.getMessage());
    }

}