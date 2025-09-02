package com.shinhanDS5gi.memento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMentoCertificationRequest {

    @NotBlank(message = "자격증 이름은 필수입니다.")
    private String name;

}