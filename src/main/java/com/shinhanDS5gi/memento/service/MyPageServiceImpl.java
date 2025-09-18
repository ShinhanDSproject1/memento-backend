package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.mypage.MyMentosByMentiResponse;
import com.shinhanDS5gi.memento.dto.mypage.MyMentosByMentiSliceResponse;
import com.shinhanDS5gi.memento.dto.mento.MentoCertificationsResponse;
import com.shinhanDS5gi.memento.dto.mypage.MyProfileResponse;
import com.shinhanDS5gi.memento.dto.mypage.UpdateMyPasswordRequest;
import com.shinhanDS5gi.memento.dto.mypage.UpdateMyProfileRequest;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;
    private final MentoCertificationService mentoCertificationService;
    private final ReviewRepository reviewRepository;

    /* 나의 프로필 정보 조회 */
    @Override
    public MyProfileResponse getMyProfile(Long memberSeq) {
        log.info("[MyPageServiceImpl.getMyProfile]");
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        List<MentoCertificationsResponse> certifications = Collections.emptyList();

        if (member.getMemberType() == MemberType.MENTO) {
            certifications = mentoCertificationService.getMentoCertifications(memberSeq);
        }

        return MyProfileResponse.builder()
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberBirthDate(member.getMemberBirthDate())
                .memberId(member.getMemberId())
                .memberType(member.getMemberType().toString())
                .certifications(certifications) // 조회된 자격증 목록 or 빈 리스트 추가
                .build();
    }

    /* 나의 프로필 정보(전화번호, 생년월일) 수정 */
    @Override
    @Transactional
    public void updateMyProfile(Long memberSeq, UpdateMyProfileRequest requestDto) {
        log.info("[MyPageServiceImpl.updateMyProfile]");
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        member.updateProfile(requestDto.getMemberPhoneNumber(), requestDto.getMemberBirthDate());
    }

    /* 나의 비밀번호 변경 */
    @Override
    @Transactional
    public void updateMyPassword(Long memberSeq, UpdateMyPasswordRequest requestDto) {
        log.info("[MyPageServiceImpl.updateMyPassword]");
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        /* 현재 비밀번호의 일치 여부 확인 */
        if(!passwordEncoder.matches(requestDto.getCurrentPassword(), member.getMemberPwd())) {
            throw new MemberException(PASSWORD_MISMATCH);
        }
        /* 새로운 비밀번호와 비밀번호 확인 값이 같은지 여부 확인 */
        if(!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new MemberException(NEW_PASSWORD_CONFIRM_MISMATCH);
        }
        /* 암호화 해서 저장 */
        member.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    /* 나의 멘토스 내역 조회 (멘티 기준) */
    @Override
    public MyMentosByMentiSliceResponse getMyMentosByMenti(Long memberSeq, int limit, Long cursor) {
        log.info("[MyPageServiceImpl.getMyMentosByMenti]");
        // 정렬된 전체 예약 목록
        List<Reservation> allReservations = reservationRepository.findAllByMemberSeqAndStatusWithSorted(memberSeq, BaseStatus.ACTIVE);

        int startIndex = 0;
        if (cursor != null) {
            for (int i = 0; i < allReservations.size(); i++) {
                if (allReservations.get(i).getReservationSeq().equals(cursor)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        // 시작 인덱스부터 필요한 만큼 데이터 slicing
        List<Reservation> slicedContent = new ArrayList<>();
        boolean hasNext = false;

        if (startIndex < allReservations.size()) {
            int endIndex = Math.min(startIndex + limit, allReservations.size());
            slicedContent = allReservations.subList(startIndex, endIndex);
            hasNext = allReservations.size() > startIndex + limit;
        }

        // DTO로 변환
        List<MyMentosByMentiResponse> content = slicedContent.stream()
                .map(res -> {
                    boolean reviewed = reviewRepository
                            .existsByReservation_ReservationSeqAndStatus(res.getReservationSeq(), BaseStatus.ACTIVE);
                    return MyMentosByMentiResponse.from(res, reviewed);
                })
                .collect(Collectors.toList());

        // 다음 페이지를 위한 cursor 값 계산
        Long nextCursor = null;
        if (!slicedContent.isEmpty()) {
            nextCursor = slicedContent.get(slicedContent.size() - 1).getReservationSeq();
        }

        return MyMentosByMentiSliceResponse.builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
