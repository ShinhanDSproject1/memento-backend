package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MentosDetailResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;

public interface MentosService {

    /* 기존 멘토스 게시글 정보 조회 */
    MentosDetailResponse getMentosById(Long mentosSeq);

    /* 멘토스 게시글 수정 */
    void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto);
}