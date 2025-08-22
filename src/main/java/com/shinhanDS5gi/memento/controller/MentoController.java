package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.CreateMentoProfileRequest;
import com.shinhanDS5gi.memento.service.MentoProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mento")
public class MentoController {

    private final MentoProfileService mentoProfileService;

    @PostMapping("/mento-profiles")
    public BaseResponse<Void> createMentoProfile(@RequestBody CreateMentoProfileRequest requestDto) {

        Long currentMemberId = 1L; // 임시 사용자 ID
        mentoProfileService.createMentoProfile(currentMemberId, requestDto);

        return new BaseResponse<>(SUCCESS, null);
    }
}
