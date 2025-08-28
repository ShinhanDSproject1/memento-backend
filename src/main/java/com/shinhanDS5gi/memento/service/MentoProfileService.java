package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateMentoProfileRequest;
import com.shinhanDS5gi.memento.dto.UpdateMentoProfileRequest;

public interface MentoProfileService {

    /* 신규 멘토 프로필 생성 */
    void createMentoProfile(Long memberSeq, CreateMentoProfileRequest requestDto);

    /* 멘토 프로필 수정 */
    void updateMentoProfile(Long memberSeq, UpdateMentoProfileRequest requestDto);
}
