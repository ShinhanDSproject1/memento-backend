package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.mentos.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MentosService {

    /* 접속한 멘토의 멘토스 목록 조회 */
    MyMentosSliceResponse<MyMentosResponse> getMyMentosSlice(Long currentMemberId, Long cursor, int limit);

    /* 멘토스 게시글 수정 */
    void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto, MultipartFile imageFile) throws IOException;

    /* 멘토스 게시글 삭제(비활성화)  */
    void inactiveMentos(Long mentosSeq, Long currentMemberSeq);

    /* 멘토스 상세조회 */
    GetMentosDetailResponse getMentosDetail(Long mentosSeq);

    /* 멘토스 전체조회(카테고리별) */
    GetMentosListResponse getMentosList(Long mentosCategorySeq, Integer limit, Long cursor);

    /* 멘토스 생성하기 */
    void createMentos(Long memberSeq, CreateMentosRequest createMentosRequest, String idemKey);

    ShowMentosDetailForUpdateResponse showMentosDetailForUpdate(Member member, Long mentosSeq);
}