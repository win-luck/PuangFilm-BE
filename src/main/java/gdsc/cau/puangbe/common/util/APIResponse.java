package gdsc.cau.puangbe.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class APIResponse<T> {

    private int code;
    private T data;
    private String msg;

    private static final int SUCCESS = 200;
    private static final int CREATED = 201;

    private APIResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    private APIResponse(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(SUCCESS, data, message);
    }

    public static <T> APIResponse<T> created(T data, String message) {
        return new APIResponse<>(CREATED, data, message);
    }

    public static <T> APIResponse<T> fail(ResponseCode responseCode, String message) {
        return new APIResponse<>(responseCode.getHttpStatus().value(), message);
    }

    public static <T> APIResponse<T> fail(ResponseCode responseCode) {
        return new APIResponse<>(responseCode.getHttpStatus().value(), responseCode.getMessage());
    }
}
