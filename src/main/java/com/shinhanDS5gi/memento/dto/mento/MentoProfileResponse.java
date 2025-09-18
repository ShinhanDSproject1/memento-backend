package com.shinhanDS5gi.memento.dto.mento;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class MentoProfileResponse {

    private String mentoProfileImage;
    private String mentoProfileContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    private String availableDays;
    private String mentoPostcode;
    private String mentoRoadAddress;
    private String mentoBname;
    private String mentoDetail;

    public MentoProfileResponse(MentoProfile mentoProfile) {
        this.mentoProfileImage = mentoProfile.getMentoProfileImage();
        this.mentoProfileContent = mentoProfile.getMentoProfileContent();
        this.startTime = mentoProfile.getStartTime();
        this.endTime = mentoProfile.getEndTime();
        this.availableDays = mentoProfile.getAvailableDays();
        this.mentoPostcode = mentoProfile.getMentoPostcode();
        this.mentoRoadAddress = mentoProfile.getMentoRoadAddress();
        this.mentoBname = mentoProfile.getMentoBname();
        this.mentoDetail = mentoProfile.getMentoDetail();
    }
}