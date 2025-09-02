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

    @NotBlank(message = "우편번호는 필수입니다.")
    private String mentosPostcode;

    @NotBlank(message = "도로명 주소는 필수입니다.")
    private String mentosRoadaddress;

    @NotBlank(message = "법정 동네 이름은 필수입니다.")
    private String mentosBname;

    private String mentosDetail;

    @NotBlank(message = "첫 번째 키워드는 필수입니다.")
    private String keywordOne;
    @NotBlank(message = "두 번째 키워드는 필수입니다.")
    private String keywordTwo;
    @NotBlank(message = "세 번째 키워드는 필수입니다.")
    private String keywordThree;
}