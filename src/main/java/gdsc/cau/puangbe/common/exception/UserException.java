package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class UserException extends BaseException {

    public UserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
