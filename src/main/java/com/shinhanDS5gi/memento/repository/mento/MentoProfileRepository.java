package com.shinhanDS5gi.memento.repository.mento;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.kakao.NearbyMentoProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentoProfileRepository extends JpaRepository<MentoProfile, Long> {

    /* memberSeq를 기준으로 멘토 프로필이 존재하는지 확인 */
    boolean existsByMember_MemberSeq(Long memberSeq);

    @Modifying(clearAutomatically = true)
    @Query("update MentoProfile mp set mp.status = :afterStatus where mp.member.memberSeq = :memberSeq and mp.status = :beforeStatus")
    int updateMentoProfileStatus(@Param("memberSeq") Long memberSeq,
                                       @Param("afterStatus") BaseStatus afterStatus,
                                       @Param("beforeStatus") BaseStatus beforeStatus);

    /* memberSeq에 맞는 mentoProfile 조회 */
    Optional<MentoProfile> findByMember_MemberSeq(Long memberSeq);

    /* 하버사인 공식을 사용해 특정 좌표(위도, 경도) 반경 내에 있는 멘토 프로필을 조회 */
    // distance : 검색할 반경 거리 (km 단위)
    // return 값 : 거리순으로 정렬된 멘토 프로필 목록
    // 하버사인이란 ? -> 두 위경도 좌표 사이의 최단거리를 구할 때 사용하는 수학적인 공식!
    @Query(value = "SELECT sub.mentoProfileSeq, sub.distance FROM (" +
            "  SELECT mp.mento_profile_seq AS mentoProfileSeq, " +
            "  (6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(mp.latitude)) * COS(RADIANS(mp.longitude) - RADIANS(:lon)) + SIN(RADIANS(:lat)) * SIN(RADIANS(mp.latitude)))) AS distance " +
            "  FROM mento_profile mp " +
            "  WHERE mp.status = 'ACTIVE' " + // <-- 바로 이 조건이 추가되었습니다!
            ") AS sub " +
            "WHERE sub.distance <= :distance " +
            "ORDER BY sub.distance",
            nativeQuery = true)
    List<NearbyMentoProfileProjection> findNearbyMentorsWithDistance(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("distance") double distance
    );

    /* 받아온 ID 목록으로 프로필과 멤버 정보를 한 번에 조회(JPQL) */
    @Query("SELECT mp FROM MentoProfile mp JOIN FETCH mp.member WHERE mp.mentoProfileSeq IN :profileSeqs")
    List<MentoProfile> findAllWithMemberBySeqIn(@Param("profileSeqs") List<Long> profileSeqs);
}
