package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;

public class KakaoMapException extends BaseException {

    public KakaoMapException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public KakaoMapException(ResponseStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}