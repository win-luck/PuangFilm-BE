package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class PhotoRequestException extends BaseException {

    public PhotoRequestException(ResponseCode responseCode) {
        super(responseCode);
    }
}
