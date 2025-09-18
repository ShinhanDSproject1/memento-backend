package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.ReservationException;
import com.shinhanDS5gi.memento.common.exception.ReviewException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
//import com.shinhanDS5gi.memento.dto.mypage.CreateReviewRequest;
import com.shinhanDS5gi.memento.dto.mento.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.mento.MentoReviewsSliceResponse;
import com.shinhanDS5gi.memento.dto.mypage.CreateReviewRequest;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.review.ReviewRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    private final IdempotencyService idempotencyService;

    /* 멘토 리뷰 조회하기 */
//    @Override
//    public MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor) {
//        // 1) 커서 조건 + limit+1로 리뷰 조회
//        var rows = reviewRepository.findMentoReviewsByCursor(mentorSeq, cursor, limit, BaseStatus.ACTIVE);
//        log.debug("조회된 리뷰 개수={}, 리뷰 목록={}", rows.size(), rows);
//        //리뷰 없을 경우
//        if (cursor == null && rows.isEmpty()) {
//            log.warn("멘토 {}의 리뷰 없음", mentorSeq);
//            throw new ReviewException(NO_REVIEWS_FOUND_FOR_MENTO);
//        }
//        // 2) 다음 페이지 여부 판단 (limit보다 많으면 hasNext = true)
//        boolean hasNext = rows.size() > limit;
//        if (hasNext) rows = rows.subList(0, limit);
//        // 3) 조회 결과를 DTO로 변환
//        var content = rows.stream()
//                .map(r -> new MentoReviewsListResponse(
//                        r.getReviewSeq(),
//                        r.getMentosTitle(),
//                        r.getReviewRating(),
//                        r.getMentiName(),
//                        r.getReviewContent(),
//                        r.getCreatedAt()
//                ))
//                .toList();
//
//        Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getReviewSeq();
//        return new MentoReviewsSliceResponse<>(content, hasNext, nextCursor);
//    }

    /* 리뷰 작성하기 */
    @Transactional
    @Override
    public void createReview(Long memberSeq, CreateReviewRequest requestDto, String idemKey) {

        log.info("[ReviewServiceImpl.createReview]==> memberSeq :"+ String.valueOf(memberSeq));
        // 멱등키 중복 여부 검사
        if (idempotencyService.isDuplicate(idemKey)) {
            throw new ReviewException(ALREADY_SUCCESS_REQUEST);
        }

        Reservation reservation = reservationRepository.findByReservationSeqAndStatus(requestDto.getReservationSeq(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new ReservationException(CANNOT_FOUND_RESERVATION));

        if (reservation.getMentosAt().isAfter(LocalDate.now())) {
            /* 진행 완료된 멘토스인가? */
            throw new ReviewException(REVIEW_NOT_ALLOWED_FOR_UNFINISHED_MENTOS);
        } else if (!reservation.getMember().getMemberSeq().equals(memberSeq)) {
            // 예약자와 요청 보낸 사용자가 동일한지 확인
            throw new ReviewException(CANNOT_CREATE_REVIEW_WITHOUT_RESERVATION);
        } else if (reviewRepository.existsById(reservation.getReservationSeq())) {
            /* 이미 리뷰를 작성한 멘토스는 아닌가? */
            throw new ReviewException(ALREADY_EXIST_REVIEW);
        }

        Review review = new Review(requestDto.getReviewRating(), requestDto.getReviewContent(), BaseStatus.ACTIVE, reservation);
        Review savedReview = reviewRepository.save(review);

        // 멱등키 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedReview.getReviewSeq()));
    }
}
