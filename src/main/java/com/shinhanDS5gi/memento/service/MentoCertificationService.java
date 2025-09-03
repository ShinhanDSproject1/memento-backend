package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.mento.CreateMentoCertificationRequest;
import com.shinhanDS5gi.memento.dto.mento.MentoCertificationsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MentoCertificationService {

    /* 내 보유 자격증 목록 조회 */
    List<MentoCertificationsResponse> getMentoCertifications(Long memberSeq);

    /* 내 자격증 추가 */
    void createMentoCertification(Long memberSeq, CreateMentoCertificationRequest requestDto, MultipartFile imageFile, String idemKey) throws IOException;
}
