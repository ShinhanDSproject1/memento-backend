package com.shinhanDS5gi.memento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMentoCertificationRequest {

    @NotBlank(message = "자격증 이름은 필수입니다.")
    private String name;

}
