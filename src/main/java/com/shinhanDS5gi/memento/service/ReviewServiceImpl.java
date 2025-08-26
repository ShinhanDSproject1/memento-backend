package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.CreateReviewRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentosRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MentosRepository mentosRepository;
    private final ReservationRepository reservationRepository;

    /* 리뷰 작성하기 */
    @Transactional
    @Override
    public void createReview(Long memberSeq, CreateReviewRequest requestDto) {
        /* 리뷰 작성자(유저), 리뷰 대상(멘토스) 조회 */
        Member reviewer = memberRepository.findById(memberSeq).orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
        Mentos reviewedMentos = mentosRepository.findById(requestDto.getMentosSeq()).orElseThrow(()-> new MentosException(CANNOT_FOUND_MENTOS));

        /* 실제로 멘토스를 예약한 유저인가? */
        Reservation reservation = reservationRepository.findByMember_MemberSeqAndMentos_MentosSeq(memberSeq, requestDto.getMentosSeq())
                .orElseThrow(() -> new MemberException(FAILURE, "해당 멘토스를 수강한 내역이 없어 리뷰를 작성할 수 없습니다."));

        /* 진행 완료된 멘토스인가? */
        if (reservation.getMentosAt().isAfter(LocalDateTime.now())) {
            throw new MemberException(FAILURE, "아직 진행하지 않은 멘토스에 대한 리뷰는 작성할 수 없습니다.");
        }

        /* 이미 리뷰를 작성한 멘토스는 아닌가? */
        if (reviewRepository.existsByMember_MemberSeqAndMentos_MentosSeq(memberSeq, requestDto.getMentosSeq())) {
            throw new MemberException(FAILURE, "이미 리뷰를 작성한 멘토스입니다.");
        }

        Review review = requestDto.toEntity(reviewer, reviewedMentos);
        reviewRepository.save(review);
    }
}
