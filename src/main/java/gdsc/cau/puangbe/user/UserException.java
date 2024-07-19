package gdsc.cau.puangbe.user;

import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ResponseCode;

public class UserException extends BaseException {

    public UserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
