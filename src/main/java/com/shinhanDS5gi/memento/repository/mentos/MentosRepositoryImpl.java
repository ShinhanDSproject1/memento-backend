package com.shinhanDS5gi.memento.repository.mentos;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shinhanDS5gi.memento.domain.QMentoCertification;
import com.shinhanDS5gi.memento.domain.QMentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MentosRepositoryImpl implements MentosCustomRepository {

    private final JPAQueryFactory queryFactory;
    QMentos mentos = QMentos.mentos;
    QMentoCertification certification = QMentoCertification.mentoCertification;

    /* 멘토스 전체조회(카테고리별) */
    @Override
    public List<GetMentosListResponse.MentosDetail> findAllByCategorySeqAndLimitAndCursor(Long mentosCategorySeq, Integer limit, Long cursor, BaseStatus status) {
        // where 절에 들어갈 쿼리문
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(mentos.status.eq(status)).and(mentos.category.categorySeq.eq(mentosCategorySeq));

        if (cursor != null) {
            builder.and(mentos.mentosSeq.lt(cursor));
        }

        BooleanExpression existsApproved = JPAExpressions.selectOne().from(certification).where(certification.member.eq(mentos.member), certification.status.eq(status)).exists();

        return queryFactory.select(Projections.fields(GetMentosListResponse.MentosDetail.class,
                        mentos.mentosSeq,
                        mentos.mentosImage.as("mentosImg"),
                        mentos.mentosTitle,
                        mentos.price.as("mentosPrice"),
                        mentos.mentosBname.as("region"),
                        new CaseBuilder().when(existsApproved).then(true).otherwise(false).as("isApproved")))
                .from(mentos)
                .where(builder)
                .orderBy(mentos.mentosSeq.desc())
                .limit(limit + 1)
                .fetch();
    }
}
