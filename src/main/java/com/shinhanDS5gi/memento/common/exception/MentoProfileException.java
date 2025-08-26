package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class MentoProfileException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public MentoProfileException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public MentoProfileException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}