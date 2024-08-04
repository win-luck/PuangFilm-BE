package gdsc.cau.puangbe.common.enums;

import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ResponseCode;

public enum Gender {
    MALE(0), FEMALE(1);

    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender fromInt(int value) {
        for (Gender gender : Gender.values()) {
            if (gender.getValue() == value) {
                return gender;
            }
        }
        throw new BaseException(ResponseCode.BAD_REQUEST);
    }
}
