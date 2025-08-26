package com.shinhanDS5gi.memento.common.exception_handler;

import com.shinhanDS5gi.memento.common.exception.MentoProfileException;
import com.shinhanDS5gi.memento.common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MentoProfileExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MentoProfileException.class)
    public BaseErrorResponse handle_MentoProfileException(MentoProfileException e) {
        log.error("[handle_MentoProfileException]", e);
        return new BaseErrorResponse(e.getExceptionStatus());
    }
}