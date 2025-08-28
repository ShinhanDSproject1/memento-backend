package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.CategoryException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Category;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MyMentosResponse;
import com.shinhanDS5gi.memento.dto.MyMentosSliceResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;
import com.shinhanDS5gi.memento.repository.CategoryRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentosServiceImpl implements MentosService {

    private final MentosRepository mentosRepository;
    private final CategoryRepository categoryRepository;

    /* 멘토가 개설한 멘토스 중 Status가 'ACTIVE'인 멘토스만 모두 조회하기 (무한스크롤 페이징 처리) */
    @Override
    public MyMentosSliceResponse<MyMentosResponse> getMyMentosSlice(Long currentMemberId, Long cursor, int limit) {
        // 첫 페이지 요청 시 cursor 초기값 설정
        Long currentCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

        // Repository 호출
        Slice<Mentos> mentosSlice = mentosRepository.findMyMentosSlice(
                currentMemberId,
                currentCursor,
                BaseStatus.ACTIVE,
                PageRequest.of(0, limit)
        );

        // 해당 멘토의 멘토스 생성 내역이 있는지 확인
        if (cursor == null && mentosSlice.getContent().isEmpty()) {
            throw new MentosException(NO_MENTOS_FOUND_FOR_MEMBER);
        }

        // 조회된 엔티티를 DTO로 변환
        List<MyMentosResponse> content = mentosSlice.getContent().stream()
                .map(mentos -> MyMentosResponse.builder()
                        .mentosSeq(mentos.getMentosSeq())
                        .mentosTitle(mentos.getMentosTitle())
                        .mentosImage(mentos.getMentosImage())
                        .price(mentos.getPrice())
                        .region(mentos.getMentosBname())
                        .build())
                .collect(Collectors.toList());

        // 다음 페이지 cursor 값 계산
        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getMentosSeq();
        }

        // 최종 응답 객체 생성
        return MyMentosSliceResponse.<MyMentosResponse>builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(mentosSlice.hasNext())
                .build();
    }

    /* 멘토스 수정하기 */
    @Override
    @Transactional
    public void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto) {
        // DB에서 Mentos 엔티티를 조회
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        // 수정 권한 확인
        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MentosException(NO_AUTHORITY_TO_UPDATE);
        }

        mentos.setMentosTitle(requestDto.getMentosTitle());
        mentos.setMentosContent(requestDto.getMentosContent());
        mentos.setPrice(requestDto.getPrice());
        mentos.setMentosImage(requestDto.getMentosImage());
        mentos.setMentosPostcode(requestDto.getMentosPostcode());
        mentos.setMentosRoadaddress(requestDto.getMentosRoadaddress());
        mentos.setMentosBname(requestDto.getMentosBname());
        mentos.setMentosDetail(requestDto.getMentosDetail());
        mentos.setKeywordOne(requestDto.getKeywordOne());
        mentos.setKeywordTwo(requestDto.getKeywordTwo());
        mentos.setKeywordThree(requestDto.getKeywordThree());
    }

    /* 멘토스 삭제하기 */
    @Override
    @Transactional
    public void inactiveMentos(Long mentosSeq, Long currentMemberId) {
        // 삭제할 멘토스 DB 조회
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        // 삭제 권한 확인
        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MentosException(NO_AUTHORITY_TO_DELETE);
        }

        mentos.setStatus(BaseStatus.INACTIVE);
    }

    /* 멘토스 전체조회(카테고리별) */
    @Override
    public GetMentosListResponse getMentosList(Long mentosCategorySeq, Integer limit, Long cursor) {
        log.info("[MentosServiceImpl.getMentosList]");

        Category category = categoryRepository.findByCategorySeqAndStatus(mentosCategorySeq, BaseStatus.ACTIVE).orElseThrow(
                () -> new CategoryException(CANNOT_FOUND_CATEGORY)
        );

        List<GetMentosListResponse.MentosDetail> mentosDetailList = mentosRepository.findAllByCategorySeqAndLimitAndCursor(mentosCategorySeq, limit, cursor, BaseStatus.ACTIVE);

        GetMentosListResponse result;
        if (mentosDetailList.size() <= limit) {
            result = GetMentosListResponse.builder().mentos(mentosDetailList.stream().limit(limit).toList()).hasNext(false).build();
        } else {
            result = GetMentosListResponse.builder().mentos(mentosDetailList.stream().limit(limit).toList()).hasNext(true).build();
        }
        return result;
    }
}