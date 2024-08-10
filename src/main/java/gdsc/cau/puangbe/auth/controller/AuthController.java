package gdsc.cau.puangbe.auth.controller;

import gdsc.cau.puangbe.auth.dto.LoginResponse;
import gdsc.cau.puangbe.auth.dto.ReissueResponse;
import gdsc.cau.puangbe.auth.service.AuthService;
import gdsc.cau.puangbe.common.util.APIResponse;
import gdsc.cau.puangbe.common.util.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "로그인, 권한 부여 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Tag(name = "auth")
    @Operation(summary = "카카오 로그인 요청 처리", description = "카카오 로그인로부터 받은 code를 BE로 전달하면 code를 통해 카카오 API를 호출하여 사용자 정보를 가져오고 Access Token을 발급한다. 클라이언트는 <Bearer [token]> 형태의 Authorization 헤더를 요청마다 첨부해야 한다.", responses = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인 성공"),
            @ApiResponse(responseCode = "404", description = "카카오 API 호출 실패")
    })
    @GetMapping("/login/oauth/kakao")
    public APIResponse<LoginResponse> loginWithKakao(@RequestParam("code") String code) {
        return APIResponse.success(authService.loginWithKakao(code), ResponseCode.USER_LOGIN_SUCCESS.getMessage());
    }

    @Tag(name = "auth")
    @Operation(summary = "토큰 재발급 요청 처리", description = "Access Token이 만료되었을 때, Refresh Token을 통해 새로운 Access Token을 발급받는다.", responses = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "토근 자체가 유효하지 않은 경우")
    })
    @GetMapping("/reissue")
    public APIResponse<ReissueResponse> reissue(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return APIResponse.success(authService.reissue(authorizationHeader), ResponseCode.USER_TOKEN_REISSUE_SUCCESS.getMessage());
    }

    @Tag(name = "auth")
    @Operation(summary = "토큰 유효성 검증 요청 처리", description = "Access Token의 유효성을 검증한다. true면 유효하며, false는 만료되었다는 의미이기에 재발급 요청해야 한다.", responses = {
            @ApiResponse(responseCode = "200", description = "토큰 유효성 검증 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 자체가 유효하지 않은 경우")
    })
    @GetMapping("/validate")
    public APIResponse<Boolean> validateToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return APIResponse.success(authService.validateToken(authorizationHeader), ResponseCode.USER_TOKEN_VALIDATE_SUCCESS.getMessage());
    }
}