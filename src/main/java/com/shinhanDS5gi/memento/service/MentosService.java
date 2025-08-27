package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MyMentosResponse;
import com.shinhanDS5gi.memento.dto.MyMentosSliceResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;

public interface MentosService {

    /* 접속한 멘토의 멘토스 목록 조회 */
    MyMentosSliceResponse<MyMentosResponse> getMyMentosSlice(Long currentMemberId, Long cursor, int limit);

    /* 멘토스 게시글 수정 */
    void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto);

    /* 멘토스 게시글 삭제(비활성화)  */
    void inactiveMentos(Long mentosSeq, Long currentMemberId);

    GetMentosListResponse getMentosList(Long mentosCategorySeq, Integer limit, Long cursor);
}