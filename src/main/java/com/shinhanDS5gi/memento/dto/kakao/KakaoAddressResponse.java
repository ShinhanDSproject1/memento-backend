package com.shinhanDS5gi.memento.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 카카오 주소 검색 API의 응답 DTO
@Getter
@NoArgsConstructor
public class KakaoAddressResponse {

    @JsonProperty("documents")
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    public static class Document {
        @JsonProperty("x")
        private Double longitude; // 경도

        @JsonProperty("y")
        private Double latitude;  // 위도
    }
}