package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class MentoCertificationException extends BaseException {

    public MentoCertificationException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public MentoCertificationException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}