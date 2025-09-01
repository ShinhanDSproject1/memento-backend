package com.shinhanDS5gi.memento.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
//멘토 자격증을 받을때 이름과, 파일을 가져올 DTO
public class MentoCertificationRequest {

    private String certificationFile;
    private String certificationName;
}
