package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class CategoryException extends RuntimeException{
    final ResponseStatus exceptionStatus;

    public CategoryException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public CategoryException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
