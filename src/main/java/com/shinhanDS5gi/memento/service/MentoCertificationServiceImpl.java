package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.config.S3Uploader;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.mento.CreateMentoCertificationRequest;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.NOT_A_MENTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoCertificationServiceImpl implements MentoCertificationService {

    private final MemberRepository memberRepository;
    private final MentoCertificationRepository mentoCertificationRepository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public void createMentoCertification(Long memberSeq, CreateMentoCertificationRequest requestDto, MultipartFile imageFile) throws IOException {
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        if (member.getMemberType() != MemberType.MENTO) {
            throw new MemberException(NOT_A_MENTO);
        }

        // null Check
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("자격증 이미지 파일은 필수입니다.");
        }

        // S3에 파일 업로드 후 URL 반환하기
        String imageUrl = s3Uploader.upload(imageFile);

        MentoCertification certification = new MentoCertification(
                null,
                requestDto.getName(),
                imageUrl,
                BaseStatus.ACTIVE,
                member
        );

        mentoCertificationRepository.save(certification);
    }
}
