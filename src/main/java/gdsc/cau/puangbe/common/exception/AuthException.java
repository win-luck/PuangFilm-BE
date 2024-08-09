package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class AuthException extends BaseException {

    public AuthException(ResponseCode responseCode) {
        super(responseCode);
    }
}
