package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentosDetailResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import com.shinhanDS5gi.memento.repository.MentosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentosServiceImpl implements MentosService {

    private final MentosRepository mentosRepository;

    @Override
    public MentosDetailResponse getMentosById(Long mentosSeq) {
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MENTOS));
        return new MentosDetailResponse(mentos);
    }

    @Override
    @Transactional
    public void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto) {
        // DB에서 Mentos 엔티티를 조회
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MENTOS));

        // 수정 권한 확인
        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MemberException(NO_AUTHORITY_TO_UPDATE);
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

    @Override
    @Transactional
    public void inactiveMentos(Long mentosSeq, Long currentMemberId) {
        // 삭제할 멘토스 DB 조회
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MENTOS));

        // 삭제 권한 확인
        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MemberException(NO_AUTHORITY_TO_DELETE);
        }

        mentos.setStatus(BaseStatus.INACTIVE);
    }
}