package com.shinhanDS5gi.memento.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetMemberListResponse {
    /**
     * 전체 회원 조회하기
     */
    private List<MemberInfo> members;
    private boolean hasNext;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private Long memberSeq;
        private String memberName;
        private String memberType;
        private LocalDate createdAt;
    }
}
