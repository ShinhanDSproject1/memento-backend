package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.MentoReviewsSliceResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

/* 멘토 리뷰 조회 (5개씩 무한 스크롤 페이징 처리) */
public class ReviewServiceImpl implements ReviewService {

    private final EntityManager em; // JPA 엔티티 매니저(쿼리 실행용)

    @Override
    // 1) 멘토별 리뷰 커서 페이징 조회
    public MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor) {
        var cb = em.getCriteriaBuilder();        // CriteriaBuilder 획득 (동적 쿼리 조립용)
        var cq = cb.createTupleQuery();          // 결과를 컬럼 묶음(튜플)으로 받는 쿼리 생성

        var r = cq.from(Review.class);                       // FROM review r
        var mt = r.join("mentos");               // JOIN r.mentos mt  (리뷰가 속한 멘토스)
        var menti = r.join("member");            // JOIN r.member m   (리뷰 작성자)

        // SELECT 절(필요한 컬럼만)
        cq.multiselect(
                r.get("reviewSeq").alias("reviewId"),
                mt.get("mentosTitle").alias("mentosTitle"),
                r.get("reviewRating").alias("reviewRating"),
                menti.get("memberName").alias("mentiName"),
                r.get("reviewContent").alias("reviewContent"),
                r.get("createdAt").alias("createdAt")
        );

        // 2) WHERE 조건 구성
        List<Predicate> where = new ArrayList<>();
        where.add(cb.equal(mt.get("member").get("memberSeq"), mentorSeq));   // 멘토가 소유한 멘토스만
        where.add(cb.equal(r.get("status"), BaseStatus.ACTIVE));             // 리뷰 상태 ACTIVE

        if (cursor != null) {                                //다음 페이지로 이동
            where.add(cb.lessThan(r.get("reviewSeq"), cursor)); // review_seq < :cursor
        }
        cq.where(where.toArray(new Predicate[0]));           // WHERE 적용

        cq.orderBy(cb.desc(r.get("reviewSeq")));             // 최신순으로 정렬

        var query = em.createQuery(cq);          // 쿼리 객체 생성
        query.setMaxResults(limit + 1);          // 다음 페이지 유무 판단
        var tuples = query.getResultList();

        // 3) 쿼리 실행 후에 첫 페이지 무데이터 예외 처리
        if (cursor == null && tuples.isEmpty()) {
            throw new MentosException(NO_REVIEWS_FOUND_FOR_MENTO);
        }

        boolean hasNext = tuples.size() > limit; // 다음 페이지 존재 여부
        if (hasNext) tuples = tuples.subList(0, limit); // 초과 1건 제거하여 정확히 limit개만 반환

        // 4) 결과를 응답 DTO로 매핑
        var content = tuples.stream()
                .map(t -> new MentoReviewsListResponse(
                        t.get("reviewId", Long.class),
                        t.get("mentosTitle", String.class),
                        t.get("reviewRating", Integer.class),
                        t.get("mentiName", String.class),
                        t.get("reviewContent", String.class),
                        t.get("createdAt", LocalDateTime.class).toLocalDate().toString()
                ))
                .toList();

        // 5) 다음 페이지 커서 계산: 마지막 항목의 reviewId 사용
        Long nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getReviewId();
        }

        // 페이지 결과 반환 및 최종 응답 객체 생성
        return new MentoReviewsSliceResponse<>(content, hasNext, nextCursor);
    }
}
