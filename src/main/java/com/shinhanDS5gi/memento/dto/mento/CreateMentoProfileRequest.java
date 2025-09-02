package com.shinhanDS5gi.memento.dto.mento;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
/* 멘토 프로필 관련 RequestDTO */
public class CreateMentoProfileRequest {

    @NotBlank(message = "멘토 소개 내용은 필수입니다.")
    private String mentoProfileContent;

    public MentoProfile toEntity(Member member, String imageUrl) {
        return new MentoProfile(
                null,
                this.mentoProfileContent,
                imageUrl,
                BaseStatus.ACTIVE,
                member
        );
    }
}
