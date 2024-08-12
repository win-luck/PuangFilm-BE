package gdsc.cau.puangbe.photorequest.controller;

import gdsc.cau.puangbe.common.annotation.PuangUser;
import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.service.PhotoRequestService;
import gdsc.cau.puangbe.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "photo-request", description = "사진 처리 요청 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/photo-request")
@RestController
public class PhotoRequestController {
    private final PhotoRequestService photoRequestService;

    // 이미지 처리 요청 생성
    @Tag(name = "photo-request")
    @Operation(summary = "유저의 이미지 처리 요청 생성", description = "유저의 AI 프로필 이미지 처리 요청을 생성한다.", responses = {
            @ApiResponse(responseCode = "200", description = "이미지 처리 요청 생성 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾지 못했을 때")
    })
    @PostMapping
    public APIResponse<Long> createImage(@Parameter(hidden = true) @PuangUser User user, @RequestBody @Valid CreateImageDto dto) {
        return APIResponse.success(photoRequestService.createImage(dto, user.getId()), ResponseCode.PHOTO_REQUEST_CREATE_SUCCESS.getMessage());
    }

    // 유저의 전체 사진 리스트 조회
    @Tag(name = "photo-request")
    @Operation(summary = "유저의 전체 사진 리스트 조회", description = "유저의 전체 사진 리스트를 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "유저의 전체 사진 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾지 못했을 때")
    })
    @GetMapping("/list")
    public APIResponse<List<String>> getRequestImages(@Parameter(hidden = true) @PuangUser User user) {
        return APIResponse.success(photoRequestService.getRequestImages(user.getId()), ResponseCode.PHOTO_LIST_FOUND.getMessage());
    }

    // 최근 생성 요청한 이미지 상태 조회
    @Tag(name = "photo-request")
    @Operation(summary = "최근 생성 요청한 이미지 상태 조회", description = "최근 생성 요청한 이미지 상태를 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "최근 생성 요청한 이미지 상태 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저의 최근 요청을 찾지 못했을 때")
    })
    @GetMapping("/status")
    public APIResponse<String> getRequestStatus(@Parameter(hidden = true) @PuangUser User user) {
        return APIResponse.success(photoRequestService.getRequestStatus(user.getId()), ResponseCode.PHOTO_STATUS_FOUND.getMessage());
    }
}
