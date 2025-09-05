package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class CreateMentosRequest {

    /**
     * 멘토스 생성하기
     */
    private String mentosTitle;
    private String mentosContent;
    private MultipartFile mentosImage;
    private Long categorySeq;
    private int price;
}
