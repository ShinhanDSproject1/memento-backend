package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateMentoCertificationRequest;

public interface MentoCertificationService {

    void createMentoCertification(Long memberSeq, CreateMentoCertificationRequest requestDto);
}
