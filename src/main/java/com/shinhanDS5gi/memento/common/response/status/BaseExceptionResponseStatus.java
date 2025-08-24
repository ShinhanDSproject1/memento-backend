package com.shinhanDS5gi.memento.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus{

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000,HttpStatus.OK.value(), "요청에 성공하였습니다."),
    FAILURE(2000, HttpStatus.BAD_REQUEST.value(), "요청에 실패하였습니다."),
    /**
     * Token 관련 code : 3000 대
     */
    INVALID_TOKEN(3000, HttpStatus.OK.value(), "유효하지 않은 토큰입니다."),

    /**
     * Auth 관련 5000대
     */
    CANNOT_SIGNUP(5000,HttpStatus.BAD_REQUEST.value(),"회원가입에 실패하셨습니다."),
    INVALID_MEMBER_ID(5001, HttpStatus.BAD_REQUEST.value(), "아이디가 올바르지 않습니다."),
    INVALID_PASSWORD(5002, HttpStatus.BAD_REQUEST.value(), "비밀번호가 올바르지 않습니다."),
    CANNOT_LOGIN(5003,HttpStatus.BAD_REQUEST.value(),"로그인에 실패하셨습니다."),
    CANNOT_FOUND_MEMBER(5004,HttpStatus.BAD_REQUEST.value(),"회원을 찾을 수가 없습니다");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
