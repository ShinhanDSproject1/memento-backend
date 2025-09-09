package com.shinhanDS5gi.memento.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class PaymentResponse {
    private String mentosTitle;
    private String mentosAt;
    private String mentosTime;
    private String availableDays;
    private  int price;
}
