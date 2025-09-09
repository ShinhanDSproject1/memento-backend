package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.kakao.NearbyMentoProfileProjection;
import com.shinhanDS5gi.memento.dto.kakao.NearbyMentosResponse;
import com.shinhanDS5gi.memento.repository.mento.MentoProfileRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapServiceImpl implements MapService {

    private final MentoProfileRepository mentoProfileRepository;
    private final MentosRepository mentosRepository;

    @Override
    public List<NearbyMentosResponse> findNearbyMentors(double latitude, double longitude, double distance) {

        // DB에서 ID와 거리 목록을 한 번에 조회
        List<NearbyMentoProfileProjection> nearbyProjections = mentoProfileRepository.findNearbyMentorsWithDistance(latitude, longitude, distance);

        if (nearbyProjections.isEmpty()) {
            return Collections.emptyList();
        }

        // 찾아진 프로필의 ID 목록과, ID별 거리 정보를 Map으로 추출
        List<Long> profileSeqs = nearbyProjections.stream().map(NearbyMentoProfileProjection::getMentoProfileSeq).toList();
        Map<Long, Double> distanceMap = nearbyProjections.stream()
                .collect(Collectors.toMap(NearbyMentoProfileProjection::getMentoProfileSeq, NearbyMentoProfileProjection::getDistance));

        // ID 목록으로 MentoProfile과 Member 정보를 한 번에 조회
        List<MentoProfile> nearbyProfiles = mentoProfileRepository.findAllWithMemberBySeqIn(profileSeqs);
        // 거리순으로 다시 정렬
        nearbyProfiles.sort(Comparator.comparing(p -> distanceMap.get(p.getMentoProfileSeq())));

        // 멘토들의 memberSeq 목록을 추출
        List<Long> mentorMemberSeqs = nearbyProfiles.stream()
                .map(p -> p.getMember().getMemberSeq())
                .toList();

        // 멘토들의 모든 Mentos 목록을 한 번에 조회
        Map<Long, List<NearbyMentosResponse.MentosInfo>> mentosMap = mentosRepository
                .findAllByMember_MemberSeqInAndStatus(mentorMemberSeqs, BaseStatus.ACTIVE)
                .stream()
                .collect(Collectors.groupingBy(
                        mentos -> mentos.getMember().getMemberSeq(),
                        Collectors.mapping(NearbyMentosResponse.MentosInfo::from, Collectors.toList())
                ));

        // 조회된 모든 데이터를 조합하여 최종 응답 DTO 생성
        return nearbyProfiles.stream()
                .map(profile -> {
                    var member = profile.getMember();
                    List<NearbyMentosResponse.MentosInfo> mentosList = mentosMap.getOrDefault(member.getMemberSeq(), Collections.emptyList());

                    return NearbyMentosResponse.builder()
                            .mentoProfileSeq(profile.getMentoProfileSeq())
                            .mentoName(member.getMemberName())
                            .mentoProfileContent(profile.getMentoProfileContent())
                            .mentoProfileImage(profile.getMentoProfileImage())
                            .latitude(profile.getLatitude())
                            .longitude(profile.getLongitude())
                            .distance(distanceMap.get(profile.getMentoProfileSeq()))
                            .mentosList(mentosList)
                            .build();
                })
                .collect(Collectors.toList());
    }
}