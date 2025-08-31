package com.shinhanDS5gi.memento.dto.mentos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class CreateMentosRequest {

    /**
     * 멘토스 생성하기
     */
    private String mentosTitle;
    private String mentosContent;
    private MultipartFile mentosImage;
    private Long categorySeq;
    private int price;
    private String mentosPostcode;
    private String mentosBname;
    private String mentosDetail;
}
