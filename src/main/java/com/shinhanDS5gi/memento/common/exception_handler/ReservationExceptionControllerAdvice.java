package com.shinhanDS5gi.memento.common.exception_handler;

import com.shinhanDS5gi.memento.common.exception.ReservationException;
import com.shinhanDS5gi.memento.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class ReservationExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReservationException.class)
    public BaseErrorResponse handle_ReservationException(ReservationException e) {
        log.error("[handle_ReservationException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
