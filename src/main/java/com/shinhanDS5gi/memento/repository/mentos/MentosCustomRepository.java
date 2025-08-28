package com.shinhanDS5gi.memento.repository.mentos;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;

import java.util.List;

public interface MentosCustomRepository {

    /* 멘토스 전체조회(카테고리별) */
    List<GetMentosListResponse.MentosDetail> findAllByCategorySeqAndLimitAndCursor(Long mentosCategorySeq, Integer limit, Long cursor, BaseStatus status);
}
