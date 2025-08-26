package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;

public interface MemberService {
    GetMemberListResponse getMemberList(Integer limit, Long cursor);
}
