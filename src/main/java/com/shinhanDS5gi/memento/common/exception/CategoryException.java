package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class CategoryException extends BaseException {

    public CategoryException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public CategoryException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}