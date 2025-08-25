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
     * Member 관련 4000대
     */
    CANNOT_FOUND_MEMBER(4000,HttpStatus.OK.value(),"해당 사용자를 찾을 수 없습니다."),
    
    /**
     * Auth 관련 5000대
     */
    CANNOT_SINGUP(5000,HttpStatus.BAD_REQUEST.value(),"회원가입에 실패했습니다.");
  
    /**
     * Report 관련 8000대
     */
    CANNOT_FOUND_REPORT(8000, HttpStatus.NOT_FOUND.value(), "해당 ID의 신고 내역을 찾을 수 없습니다.");

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
