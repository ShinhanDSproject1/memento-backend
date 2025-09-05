package com.shinhanDS5gi.memento.dto.mentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 멘토스 게시글 수정을 위한 요청 DTO */
@Getter
@NoArgsConstructor
public class UpdateMentosRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String mentosTitle;

    @NotBlank(message = "내용은 필수입니다.")
    private String mentosContent;

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    private Integer price;

}