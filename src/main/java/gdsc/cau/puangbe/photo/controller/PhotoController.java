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

}