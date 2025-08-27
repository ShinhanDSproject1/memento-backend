package com.shinhanDS5gi.memento.repository.mentos;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shinhanDS5gi.memento.domain.QMentoCertification;
import com.shinhanDS5gi.memento.domain.QMentoProfile;
import com.shinhanDS5gi.memento.domain.QMentos;
import com.shinhanDS5gi.memento.domain.QReview;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.QMember;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosDetailProjection;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosDetailResponse;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MentosRepositoryImpl implements MentosCustomRepository {

    private final JPAQueryFactory queryFactory;
    QMentos mentos = QMentos.mentos;
    QReview review = QReview.review;
    QMentoCertification certification = QMentoCertification.mentoCertification;
    QMember member = QMember.member;
    QMentoProfile mentoProfile = QMentoProfile.mentoProfile;

    /* 멘토스 전체조회(카테고리별) */
    @Override
    public List<GetMentosListResponse.MentosDetail> findAllByCategorySeqAndLimitAndCursor(Long mentosCategorySeq, Integer limit, Long cursor, BaseStatus status) {
        // where 절에 들어갈 쿼리문
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(mentos.status.eq(status)).and(mentos.category.categorySeq.eq(mentosCategorySeq));

        if (cursor != null) {
            builder.and(mentos.mentosSeq.lt(cursor));
        }

        BooleanExpression existsApproved = JPAExpressions.selectOne().from(certification).where(certification.member.memberSeq.eq(mentos.member.memberSeq), certification.status.eq(status)).exists();

        return queryFactory.select(Projections.fields(GetMentosListResponse.MentosDetail.class,
                        mentos.mentosSeq,
                        mentos.mentosImage.as("mentosImg"),
                        mentos.mentosTitle,
                        mentos.price.as("mentosPrice"),
                        mentos.mentosBname.as("region"),
                        new CaseBuilder().when(existsApproved).then(true).otherwise(false).as("approved")))
                .from(mentos)
                .where(builder)
                .orderBy(mentos.mentosSeq.desc())
                .limit(limit + 1)
                .fetch();
    }

    /* 멘토스 상세조회 */
    @Override
    public GetMentosDetailProjection findMentosDetailByMentosSeqAndStatus(Long mentosSeq, BaseStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(mentos.mentosSeq.eq(mentosSeq)).and(mentos.status.eq(status));

        SubQueryExpression<Integer> reviewTotalCnt = JPAExpressions.select(review.reviewSeq.count().intValue()).from(review).where(review.mentos.mentosSeq.eq(mentosSeq).and(review.status.eq(status)));
        SubQueryExpression<Double> reviewRatingAvg = JPAExpressions.select(review.reviewRating.avg()).from(review).where(review.mentos.mentosSeq.eq(mentosSeq).and(review.status.eq(status)));

        return queryFactory.select(Projections.constructor(
                        GetMentosDetailProjection.class,
                        mentos.mentosImage,
                        mentos.mentosTitle,
                        Expressions.stringTemplate(
                                "concat({0}, ' ', coalesce({1}, ''))",
                                mentos.mentosRoadaddress, mentos.mentosDetail
                        ).as("mentosLocation"),
                        reviewTotalCnt,
                        reviewRatingAvg,
                        member.memberName.as("mentoName"),
                        mentoProfile.mentoProfileImage.as("mentoImg"),
                        mentoProfile.mentoProfileContent.as("mentoDescription"),
                        mentos.mentosContent.as("mentosDescription"),
                        mentos.price.as("mentosPrice")
                )).from(mentos).join(mentos.member, member).leftJoin(mentoProfile)
                .on(mentoProfile.member.eq(member))
                .where(builder).fetchOne();
    }
}