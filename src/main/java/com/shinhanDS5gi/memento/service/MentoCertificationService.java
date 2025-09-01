package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateMentoCertificationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MentoCertificationService {

    void createMentoCertification(Long memberSeq, CreateMentoCertificationRequest requestDto, MultipartFile imageFile) throws IOException;
}
