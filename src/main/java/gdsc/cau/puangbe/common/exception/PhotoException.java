package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class PhotoException extends BaseException {

    public PhotoException(ResponseCode responseCode) {
        super(responseCode);
    }
}
