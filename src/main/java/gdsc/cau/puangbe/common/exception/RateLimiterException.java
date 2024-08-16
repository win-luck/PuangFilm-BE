package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class RateLimiterException extends BaseException {
    public RateLimiterException(ResponseCode responseCode) {
        super(responseCode);
    }
}
