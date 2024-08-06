package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public APIResponse<Void> handleBaseException(BaseException e) {
        log.info("BaseException: {}", e.getMessage());
        return APIResponse.fail(e.getResponseCode(), e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public APIResponse<Void> handleUserException(UserException e) {
        log.info("UserException: {}", e.getMessage());
        return APIResponse.fail(e.getResponseCode(), e.getMessage());
    }
}
