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
     * Mentos 관련 code : 2500 대
     */
    CANNOT_FOUND_MENTOS(2500, HttpStatus.NOT_FOUND.value(), "해당 멘토스 게시글을 찾을 수 없습니다."),
    NO_AUTHORITY_TO_UPDATE(2501, HttpStatus.FORBIDDEN.value(), "게시글을 수정할 권한이 없습니다."),
    NO_AUTHORITY_TO_DELETE(2502, HttpStatus.FORBIDDEN.value(), "게시글을 삭제할 권한이 없습니다."),

    /**
     * Token 관련 code : 3000 대
     */
    INVALID_TOKEN(3000, HttpStatus.OK.value(), "유효하지 않은 토큰입니다.");




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
