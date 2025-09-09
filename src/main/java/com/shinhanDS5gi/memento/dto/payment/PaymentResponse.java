package com.shinhanDS5gi.memento.dto.payment;


import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PaymentResponse {
    private String mentosTitle;
    private String mentosAt;
    private String mentosTime;
    private String dayOfWeek;
    private  int price;
}
