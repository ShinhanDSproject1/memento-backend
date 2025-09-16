package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class MemberException extends BaseException {

    public MemberException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public MemberException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}