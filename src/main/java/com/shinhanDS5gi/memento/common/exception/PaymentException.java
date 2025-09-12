package com.shinhanDS5gi.memento.common.exception;

import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final ResponseStatus exceptionStatus;
    private final String detailMessage; // 상세 메시지를 담을 필드 추가

    public PaymentException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
        this.detailMessage = exceptionStatus.getMessage();
    }

    // 상세 메시지를 함께 받는 새로운 생성자 추가
    public PaymentException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage); // Exception의 메시지는 상세 메시지로 설정
        this.exceptionStatus = exceptionStatus;
        this.detailMessage = detailMessage;
    }
}
