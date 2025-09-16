package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class PaymentException extends BaseException {

    public PaymentException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public PaymentException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}