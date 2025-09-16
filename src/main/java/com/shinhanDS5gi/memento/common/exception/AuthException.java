package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class AuthException extends BaseException {

    public AuthException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public AuthException(ResponseStatus exceptionStatus, String detailMessage) {
        super(exceptionStatus, detailMessage);
    }
}