package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MemberService memberService;

    @GetMapping("/member")
    public BaseResponse<GetMemberListResponse> getMemberList(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "cursor", required = false) Long cursor){
        // 여기서 입력 받는 cursor 값은 Long 타입의 memberSeq
        log.info("[AdminController.getMemberList]");
        return new BaseResponse<>(memberService.getMemberList(limit,cursor));
    }

}
