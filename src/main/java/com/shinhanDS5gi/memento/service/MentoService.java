package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.mento.MyMentiResponse;

import java.util.List;

public interface MentoService {

    /* 현재 멘토의 멘티 목록을 멘토스별로 그룹화해 조회 */
    List<MyMentiResponse> getMyMentiList(Long currentMemberId);
}
