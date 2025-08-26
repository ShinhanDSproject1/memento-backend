package com.shinhanDS5gi.memento.service;


import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.MentoReviewsSliceResponse;
import com.shinhanDS5gi.memento.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.NO_REVIEWS_FOUND_FOR_MENTO;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    /* 멘토 리뷰 조회하기 */
    @Override
    public MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor) {
        // 1) 커서 조건 + limit+1로 리뷰 조회
        var rows = reviewRepository.findMentoReviewsByCursor(mentorSeq, cursor, limit, BaseStatus.ACTIVE);

        //리뷰 없을 경우
        if (cursor == null && rows.isEmpty()) {
            throw new MentosException(NO_REVIEWS_FOUND_FOR_MENTO);
        }
        // 2) 다음 페이지 여부 판단 (limit보다 많으면 hasNext = true)
        boolean hasNext = rows.size() > limit;
        if (hasNext) rows = rows.subList(0, limit);
        // 3) 조회 결과를 DTO로 변환
        var fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var content = rows.stream()
                .map(r -> new MentoReviewsListResponse(
                        r.getReviewId(),
                        r.getMentosTitle(),
                        r.getReviewRating(),
                        r.getMentiName(),
                        r.getReviewContent(),
                        r.getCreatedAt()
                ))
                .toList();

        Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getReviewId();
        return new MentoReviewsSliceResponse<>(content, hasNext, nextCursor);
    }
}