package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.MyMentosByMentiResponse;
import com.shinhanDS5gi.memento.dto.MyMentosByMentiSliceResponse;
import com.shinhanDS5gi.memento.dto.MyProfileResponse;
import com.shinhanDS5gi.memento.dto.UpdateMyPasswordRequest;
import com.shinhanDS5gi.memento.dto.UpdateMyProfileRequest;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;

    /* 나의 프로필 정보 조회 */
    @Override
    public MyProfileResponse getMyProfile(Long memberSeq) {

        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        return MyProfileResponse.builder()
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberBirthDate(member.getMemberBirthDate())
                .memberId(member.getMemberId())
                .build();
    }

    /* 나의 프로필 정보(전화번호, 생년월일) 수정 */
    @Override
    @Transactional
    public void updateMyProfile(Long memberSeq, UpdateMyProfileRequest requestDto) {
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        member.updateProfile(requestDto.getMemberPhoneNumber(), requestDto.getMemberBirthDate());
    }

    /* 나의 비밀번호 변경 */
    @Override
    @Transactional
    public void updateMyPassword(Long memberSeq, UpdateMyPasswordRequest requestDto) {
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
        Long currentCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

        Slice<Reservation> reservationSlice = reservationRepository.findByMemberSeqWithSlice(
                memberSeq, currentCursor, BaseStatus.ACTIVE, PageRequest.of(0, limit)
        );

        List<MyMentosByMentiResponse> content = reservationSlice.getContent().stream()
                .map(MyMentosByMentiResponse::from)
                .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            Reservation lastReservation = reservationSlice.getContent().get(reservationSlice.getContent().size() - 1);
            nextCursor = lastReservation.getReservationSeq();
        }

        return MyMentosByMentiSliceResponse.builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(reservationSlice.hasNext())
                .build();
    }
}
