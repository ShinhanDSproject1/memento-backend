package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class ReviewException extends BaseException {

    public ReviewException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public ReviewException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}