package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
//멘토 자격증을 받을때 이름과, 파일을 가져올 DTO
public class MentoCertificationRequest {

    private String certificationFile;
    private String certificationName;
}
