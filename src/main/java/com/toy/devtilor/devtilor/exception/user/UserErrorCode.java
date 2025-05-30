package com.toy.devtilor.devtilor.exception.user;


import com.toy.devtilor.devtilor.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "해당 유저가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
