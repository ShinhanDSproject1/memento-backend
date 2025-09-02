package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.ReviewException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.CreateReviewRequest;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.MentoReviewsSliceResponse;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.Review.ReviewRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MentosRepository mentosRepository;
    private final ReservationRepository reservationRepository;

    /* 멘토 리뷰 조회하기 */
    @Override
    public MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor) {
        // 1) 커서 조건 + limit+1로 리뷰 조회
        var rows = reviewRepository.findMentoReviewsByCursor(mentorSeq, cursor, limit, BaseStatus.ACTIVE);
        log.debug("조회된 리뷰 개수={}, 리뷰 목록={}", rows.size(), rows);
        //리뷰 없을 경우
        if (cursor == null && rows.isEmpty()) {
            log.warn("멘토 {}의 리뷰 없음", mentorSeq);
            throw new ReviewException(NO_REVIEWS_FOUND_FOR_MENTO);
        }
        // 2) 다음 페이지 여부 판단 (limit보다 많으면 hasNext = true)
        boolean hasNext = rows.size() > limit;
        if (hasNext) rows = rows.subList(0, limit);
        // 3) 조회 결과를 DTO로 변환
        var content = rows.stream()
                .map(r -> new MentoReviewsListResponse(
                        r.getReviewSeq(),
                        r.getMentosTitle(),
                        r.getReviewRating(),
                        r.getMentiName(),
                        r.getReviewContent(),
                        r.getCreatedAt()
                ))
                .toList();

        Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getReviewSeq();
        return new MentoReviewsSliceResponse<>(content, hasNext, nextCursor);
    }

    /* 리뷰 작성하기 */
    @Transactional
    @Override
    public void createReview(Long memberSeq, CreateReviewRequest requestDto) {
        /* 리뷰 작성자(유저), 리뷰 대상(멘토스) 조회 */
        Member reviewer = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
        Mentos reviewedMentos = mentosRepository.findByMentosSeqAndStatus(requestDto.getMentosSeq(), BaseStatus.ACTIVE)
                .orElseThrow(()-> new MentosException(CANNOT_FOUND_MENTOS));

        /* 실제로 멘토스를 예약한 유저인가? */
        Reservation reservation = reservationRepository.findByMember_MemberSeqAndMentos_MentosSeqAndStatus(memberSeq, requestDto.getMentosSeq(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new ReviewException(FAILURE, "해당 멘토스를 수강한 내역이 없어 리뷰를 작성할 수 없습니다."));

        /* 진행 완료된 멘토스인가? */
        if (reservation.getMentosAt().isAfter(LocalDateTime.now())) {
            throw new ReviewException(FAILURE, "아직 진행하지 않은 멘토스에 대한 리뷰는 작성할 수 없습니다.");
        }

        /* 이미 리뷰를 작성한 멘토스는 아닌가? */
        if (reviewRepository.existsByMember_MemberSeqAndMentos_MentosSeq(memberSeq, requestDto.getMentosSeq())) {
            throw new ReviewException(FAILURE, "이미 리뷰를 작성한 멘토스입니다.");
        }

        Review review = requestDto.toEntity(reviewer, reviewedMentos);
        reviewRepository.save(review);
    }
}
