package gdsc.cau.puangbe.photorequest.controller;

import gdsc.cau.puangbe.common.util.ApiResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.dto.ResponseResultDto;
import gdsc.cau.puangbe.photorequest.service.PhotoRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/photo-request")
@RestController
public class PhotoRequestController {
    private final PhotoRequestService photoRequestService;

    // 이미지 처리 요청 생성
    @PostMapping
    public ApiResponse<Void> createImage(@RequestBody CreateImageDto dto, @RequestParam Long userId) {
        photoRequestService.createImage(dto, userId);
        return ApiResponse.success(null, ResponseCode.PHOTO_REQUEST_CREATE_SUCCESS.getMessage());
    }

    // 유저의 전체 사진 리스트 조회
    @GetMapping("/list")
    public ApiResponse<ResponseResultDto> getRequestImages(@RequestParam Long userId) {
        return ApiResponse.success(photoRequestService.getRequestImages(userId), ResponseCode.PHOTO_LIST_FOUND.getMessage());
    }

    // 최근 생성 요청한 이미지 상태 조회
    @GetMapping("/check")
    public ApiResponse<String> getRequestStatus(@RequestParam Long userId) {
        return ApiResponse.success(photoRequestService.getRequestStatus(userId), ResponseCode.PHOTO_STATUS_FOUND.getMessage());
    }
}
