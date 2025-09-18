package com.shinhanDS5gi.memento.repository.review;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shinhanDS5gi.memento.domain.QMentos;
import com.shinhanDS5gi.memento.domain.QReservation;
import com.shinhanDS5gi.memento.domain.QReview;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.QMember;
import com.shinhanDS5gi.memento.dto.mento.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.mentos.GetReviewListForMentosDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {

    // QueryDSL 쿼리 팩토리 주입
    private final JPAQueryFactory queryFactory;

    QReview review = QReview.review;
    QMentos mentos = QMentos.mentos;
    QMember member = QMember.member;
    QReservation reservation = QReservation.reservation;

    @Override
    public List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentoSeq, Long cursor, int limit, BaseStatus status) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(review.reservation.mentos.mentosSeq.eq(mentoSeq))
                .and(review.status.eq(status));

        if (cursor != null) {
            builder.and(review.reviewSeq.lt(cursor));
        }

        // 필요한 컬럼만 DTO로 '생성자 프로젝션' 해서 조회
        return queryFactory.select(Projections.constructor(MentoReviewsListResponse.class,
                        review.reviewSeq,
                        mentos.mentosTitle,
                        review.reviewRating,
                        member.memberName,
                        review.reviewContent,
                        Expressions.stringTemplate("DATE_FORMAT({0}, {1})", review.createdAt, "%Y-%m-%d")))
                .from(review)
                .join(review.reservation, reservation)
                .join(reservation.mentos, mentos)
                .join(reservation.member, member)
                .where(builder)
                .orderBy(review.reviewSeq.desc())
                .limit(limit + 1)
                .fetch();
    }

    /* 멘토스 상세조회에 쓰이는 review  */
    @Override
    public List<GetReviewListForMentosDetailResponse.Review> findReviewByMentosSeqAndLimitAndCursorAndStatus(Long mentosSeq, Integer limit, Long cursor, BaseStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(review.reservation.mentos.mentosSeq.eq(mentosSeq)).and(review.status.eq(status));

        if (cursor != null) {
            builder.and(review.reviewSeq.lt(cursor));
        }

        return queryFactory
                .select(Projections.constructor(GetReviewListForMentosDetailResponse.Review.class,
                        review.reviewSeq,
                        review.reviewRating,
                        // MySQL DATE_FORMAT 그대로 사용
                        Expressions.stringTemplate("date_format({0}, {1})",
                                review.createdAt, Expressions.constant("%Y-%m-%d")),
                        review.reviewContent
                ))
                .from(review)
                .join(review.reservation, reservation)
                .where(builder)
                .orderBy(review.reviewSeq.desc())
                .limit(limit+1)
                .fetch();
    }

}
