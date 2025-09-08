package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.kakao.NearbyMentosResponse;
import java.util.List;

public interface MapService {

    /* 유저의 주변 멘토(멘토스) 조회 */
    List<NearbyMentosResponse> findNearbyMentors(double latitude, double longitude, double distance);
}