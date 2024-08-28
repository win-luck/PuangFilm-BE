package gdsc.cau.puangbe.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ResponseCode {

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, false, "잘못된 요청입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, false, "인증되지 않은 사용자입니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, false, "권한이 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, false, "리프레시 토큰이 만료되었습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "사용자를 찾을 수 없습니다."),
    PHOTO_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, false, "요청 객체를 찾을 수 없습니다."),
    PHOTO_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "이미지 객체를 찾을 수 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, false, "허용되지 않은 메서드입니다."),

    // 409 Conflict
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, false, "이미 존재하는 사용자입니다."),
    URL_ALREADY_UPLOADED(HttpStatus.CONFLICT, false, "이미 url이 업로드 되었습니다."),

    // 429 Too Many Requests
    RATE_LIMITER_TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, false, "호출 허용량 초과입니다. 잠시 후 다시 시도해 주세요."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "서버에 오류가 발생하였습니다."),
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "JSON 파싱 오류가 발생하였습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "이메일 발송에 오류가 발생하였습니다."),
    RATE_LIMITER_POLICY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "정의되지 않은 정책입니다."),

    // 200 OK
    USER_LOGIN_SUCCESS(HttpStatus.OK, true, "사용자 로그인 성공"),
    USER_TOKEN_REISSUE_SUCCESS(HttpStatus.OK, true, "사용자 토큰 재발급 성공"),
    USER_TOKEN_VALIDATE_SUCCESS(HttpStatus.OK, true, "사용자 토큰 검증 성공"),
    PHOTO_RESULT_URL_FOUND(HttpStatus.OK, true, "image url 조회 성공"),
    PHOTO_STATUS_FOUND(HttpStatus.OK, true, "resultImage 상태 조회 성공"),
    PHOTO_LIST_FOUND(HttpStatus.OK, true, "resultImage List 조회 성공"),

    // 201 Created
    USER_CREATE_SUCCESS(HttpStatus.CREATED, true, "사용자 생성 성공"),
    PHOTO_REQUEST_CREATE_SUCCESS(HttpStatus.CREATED, true, "사진 요청 생성 성공"),
    PHOTO_RESULT_CREATE_SUCCESS(HttpStatus.CREATED, true, "PhotoResult 생성 성공"),
    PHOTO_RESULT_URL_UPLOADED(HttpStatus.CREATED, true, "PhotoResult url 업로드 성공"),

    // 202 Accepted
    IMAGE_ON_PROCESS(HttpStatus.ACCEPTED, true, "해당 이미지는 처리 중입니다.");

    private final HttpStatus httpStatus;
    private final Boolean success;
    private final String message;
}
