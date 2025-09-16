package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class MentosException extends BaseException {

    public MentosException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public MentosException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}