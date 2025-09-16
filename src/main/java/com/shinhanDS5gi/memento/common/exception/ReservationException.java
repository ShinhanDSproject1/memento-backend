package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class ReservationException extends BaseException {

    public ReservationException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public ReservationException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}