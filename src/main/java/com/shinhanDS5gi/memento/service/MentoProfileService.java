package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.mento.CreateMentoProfileRequest;
import com.shinhanDS5gi.memento.dto.mento.MentoProfileResponse;
import com.shinhanDS5gi.memento.dto.mento.UpdateMentoProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MentoProfileService {

    /* 신규 멘토 프로필 생성 */
    void createMentoProfile(Long memberSeq, CreateMentoProfileRequest requestDto, MultipartFile imageFile, String idemKey) throws IOException;

    /* 멘토 프로필 수정 */
    void updateMentoProfile(Long memberSeq, UpdateMentoProfileRequest requestDto, MultipartFile imageFile) throws IOException;

    /* 멘토 프로필 조회 */
    MentoProfileResponse getMentoProfile(Long memberSeq);
}
