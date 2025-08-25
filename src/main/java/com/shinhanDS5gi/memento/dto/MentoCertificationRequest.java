package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
//멘토 자격증받을때 이름과, 파일를 가져올 DTO
public class MentoCertificationRequest {
    // "certificationFile": "저축소비.pdf"
    private String certificationFile;

    // "certificationName": "자격증이름"
    private String certificationName;

}
