package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateMentoCertificationRequest {

    @Valid
    private List<CertificationInfo> certifications;

    /* 개별 자격증의 정보를 담는 내부 DTO 클래스 */
    @Getter
    @NoArgsConstructor
    public static class CertificationInfo {

        @NotBlank(message = "자격증 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "자격증 이미지는 필수입니다.")
        private String imageUrl;

        public MentoCertification toEntity(Member member) {
            return new MentoCertification(
                    null,
                    this.name,
                    this.imageUrl,
                    BaseStatus.ACTIVE,
                    member
            );
        }
    }
}
