package com.shinhanDS5gi.memento.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.QMember;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    QMember member = QMember.member;

    @Override
    public List<Member> findAllByIdAndLimitAndCursor(Integer limit, Long cursor, BaseStatus status) {
        // where 절에 들어갈 쿼리문
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.status.eq(status));

        if (cursor != null) {
            // 보내준 데이터의 마지막 memberSeq 보다 작은 데이터를 데려오기 위한 where 조건문
            builder.and(member.memberSeq.lt(cursor));
        }

        return queryFactory.select(Projections.fields(Member.class,
                        member.memberSeq,
                        member.memberName,
                        member.memberType,
                        member.createdAt))
                .from(member)
                .where(builder)
                .orderBy(member.memberSeq.desc())
                .limit(limit)
                .fetch();
    }
}
