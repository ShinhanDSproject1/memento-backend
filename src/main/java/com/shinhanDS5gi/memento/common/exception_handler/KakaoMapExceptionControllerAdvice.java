package com.shinhanDS5gi.memento.common.exception_handler;

import com.shinhanDS5gi.memento.common.exception.KakaoMapException;
import com.shinhanDS5gi.memento.common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class KakaoMapExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(KakaoMapException.class)
    public BaseErrorResponse handle_KakaoMapException(KakaoMapException e) {
        log.error("[handle_KakaoMapException]", e);
        // enum에 정의된 실제 상태 코드를 사용하여 응답을 생성
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}