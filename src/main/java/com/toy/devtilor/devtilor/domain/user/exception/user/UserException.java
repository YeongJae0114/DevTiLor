package com.toy.devtilor.devtilor.domain.user.exception.user;


import com.toy.devtilor.devtilor.common.exception.BusinessException;
import com.toy.devtilor.devtilor.common.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static UserException userNotFound() {return new UserException(UserErrorCode.USER_NOT_FOUND);}

}