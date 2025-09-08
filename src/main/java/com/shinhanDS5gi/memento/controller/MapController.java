package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.kakao.NearbyMentosResponse;
import com.shinhanDS5gi.memento.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    @GetMapping("/mentos")
    public BaseResponse<List<NearbyMentosResponse>> getNearbyMentors(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "3.0") double distance) {

        List<NearbyMentosResponse> nearbyMentors = mapService.findNearbyMentors(latitude, longitude, distance);
        return new BaseResponse<>(SUCCESS, nearbyMentors);
    }
}