package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class ReportException extends BaseException {

    public ReportException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public ReportException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}