package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.KakaoMapException;
import com.shinhanDS5gi.memento.dto.kakao.KakaoAddressResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.INVALID_ADDRESS_ERROR;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.KAKAO_API_CONNECTION_ERROR;

@Service
public class KakaoMapService {

    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    /* 도로명 주소를 받아서 [경도, 위도] 배열을 반환하는 메서드 */
    public double[] getCoordinates(String roadAddress) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = KAKAO_API_URL + "?query=" + roadAddress;

        try {
            KakaoAddressResponse response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoAddressResponse.class).getBody();

            // 주소 검색 결과가 없는 경우
            if (response == null || response.getDocuments().isEmpty()) {
                throw new KakaoMapException(INVALID_ADDRESS_ERROR);
            }

            KakaoAddressResponse.Document firstDocument = response.getDocuments().get(0);
            return new double[]{firstDocument.getLongitude(), firstDocument.getLatitude()};

        } catch (Exception e) {
            // API 호출 자체에 실패한 경우
            throw new KakaoMapException(KAKAO_API_CONNECTION_ERROR);
        }
    }
}