package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MyProfileResponse;
import com.shinhanDS5gi.memento.dto.UpdateMyPasswordRequest;
import com.shinhanDS5gi.memento.dto.UpdateMyProfileRequest;

public interface MyPageService {

    /* 나의 프로필 정보 조회 */
    MyProfileResponse getMyProfile(Long memberSeq);

    /* 나의 프로필(전화번호, 생년월일) 수정 */
    void updateMyProfile(Long memberSeq, UpdateMyProfileRequest requestDto);

    /* 나의 비밀번호 변경 */
    void updateMyPassword(Long memberSeq, UpdateMyPasswordRequest requestDto);
}
