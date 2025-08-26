package com.shinhanDS5gi.memento.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shinhanDS5gi.memento.domain.QMentos;
import com.shinhanDS5gi.memento.domain.QReview;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.QMember;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository{

    // QueryDSL 쿼리 팩토리 주입
    private final JPAQueryFactory queryFactory;

    QReview r = QReview.review;
    QMentos mt = QMentos.mentos;
    QMember m = QMember.member;

    @Override
    public List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status) {
        var builder = new BooleanBuilder()
                .and(mt.member.memberSeq.eq(mentorSeq))
                .and(r.status.eq(status));
        if (cursor != null){

            builder.and(r.reviewSeq.lt(cursor));
    }
        // 필요한 컬럼만 DTO로 '생성자 프로젝션' 해서 조회
        return queryFactory.select(Projections.constructor(MentoReviewsListResponse.class,
                        r.reviewSeq,
                        mt.mentosTitle,
                        r.reviewRating,
                        m.memberName,
                        r.reviewContent,
                        r.createdAt))
                .from(r)
                .join(r.mentos, mt)
                .join(r.member, m)
                .where(builder)
                .orderBy(r.reviewSeq.desc())
                .limit(limit + 1)
                .fetch();
    }
}
