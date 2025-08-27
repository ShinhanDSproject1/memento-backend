package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MyMentosByMentiSliceResponse;
import com.shinhanDS5gi.memento.dto.MyProfileResponse;

public interface MyPageService {

    /* 나의 프로필 정보 조회 */
    MyProfileResponse getMyProfile(Long memberSeq);

    /* 나의 멘토스 내역 조회(멘티 기준) */
    MyMentosByMentiSliceResponse getMyMentosByMenti(Long memberSeq, int limit, Long cursor);
}
