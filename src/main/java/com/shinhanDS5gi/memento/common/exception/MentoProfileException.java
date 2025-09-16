package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class MentoProfileException extends BaseException {

    public MentoProfileException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public MentoProfileException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}