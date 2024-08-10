package gdsc.cau.puangbe.photo.controller;

import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.UploadImageDto;
import gdsc.cau.puangbe.photo.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "photo", description = "사진 처리 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    private final PhotoService photoService;

    @Tag(name = "photo")
    @Operation(summary = "생성된 AI 프로필 ImageURL 업로드", description = "AI 모듈에서 만든 결과 이미지의 URL을 DB로 업로드한다.", responses = {
            @ApiResponse(responseCode = "200", description = "사진 업로드 성공"),
            @ApiResponse(responseCode = "404", description = "photoResultId가 유효하지 않은 경우"),
            @ApiResponse(responseCode = "409", description = "이미 url이 업로드되어 종료 상태인 경우")
    })
    @PostMapping("/url")
    public APIResponse<Void> uploadImage(@RequestBody @Valid UploadImageDto uploadImageDto) {
        photoService.uploadPhoto(uploadImageDto.getPhotoRequestId(), uploadImageDto.getImageUrl());
        return APIResponse.success(null, ResponseCode.PHOTO_RESULT_URL_UPLOADED.getMessage());
    }
    
    @Tag(name = "photo")
    @Operation(summary = "사진 URL 조회", description = "특정 요청 id를 통해 요청의 결과 이미지의 URL을 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "사진 URL 조회 성공"),
            @ApiResponse(responseCode = "404", description = "photoRequestId가 유효하지 않은 경우"),
    })
    @GetMapping("/{imageId}")
    public APIResponse<String> getImage(@PathVariable("imageId") Long photoRequestId) {
        return APIResponse.success(photoService.getPhotoUrl(photoRequestId), ResponseCode.PHOTO_RESULT_URL_FOUND.getMessage());
    }
}
