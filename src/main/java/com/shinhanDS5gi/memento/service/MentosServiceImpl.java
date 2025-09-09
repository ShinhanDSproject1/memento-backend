package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.CategoryException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.config.S3Uploader;
import com.shinhanDS5gi.memento.domain.Category;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.repository.MentoProfileRepository;
import com.shinhanDS5gi.memento.repository.CategoryRepository;
import com.shinhanDS5gi.memento.repository.review.ReviewRepository;
import com.shinhanDS5gi.memento.dto.mentos.MyMentosResponse;
import com.shinhanDS5gi.memento.dto.mentos.MyMentosSliceResponse;
import com.shinhanDS5gi.memento.dto.mentos.UpdateMentosRequest;
import com.shinhanDS5gi.memento.dto.mentos.CreateMentosRequest;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosDetailResponse;

import com.shinhanDS5gi.memento.dto.mentos.GetMentosListResponse;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import com.shinhanDS5gi.memento.repository.mento.MentoProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentosServiceImpl implements MentosService {

    private final MemberRepository memberRepository;
    private final MentosRepository mentosRepository;
    private final CategoryRepository categoryRepository;
    private final MentoProfileRepository mentoProfileRepository;
    private final ReviewRepository reviewRepository;

    private final S3Uploader s3Uploader;
    private final IdempotencyService idempotencyService;

    /* 멘토가 개설한 멘토스 중 Status가 'ACTIVE'인 멘토스만 모두 조회하기 (무한스크롤 페이징 처리) */
    @Override
    public MyMentosSliceResponse<MyMentosResponse> getMyMentosSlice(Long currentMemberId, Long cursor, int limit) {
        // 첫 페이지 요청 시 cursor 초기값 설정
        Long currentCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

        // 멘토의 프로필의 장소 정보 조회
        MentoProfile mentoProfile = mentoProfileRepository.findByMember_MemberSeq(currentMemberId)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTO_PROFILE));
        String region = mentoProfile.getMentoBname();

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
                        .region(region)
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
    public void updateMentos(Long mentosSeq, Long currentMemberId, UpdateMentosRequest requestDto, MultipartFile imageFile) throws IOException {
        Mentos mentos = mentosRepository.findByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MentosException(NO_AUTHORITY_TO_UPDATE);
        }

        String newImageUrl = null;

        // null이 아니다? 기존 이미지 삭제 후 새로운 이미지 넣기
        if (imageFile != null && !imageFile.isEmpty()) {
            s3Uploader.delete(mentos.getMentosImage());
            newImageUrl = s3Uploader.upload(imageFile);
        }

        mentos.update(requestDto, newImageUrl);
    }

    /* 멘토스 삭제하기 */
    @Override
    @Transactional
    public void inactiveMentos(Long mentosSeq, Long currentMemberId) {
        // 삭제할 멘토스 DB 조회
        Mentos mentos = mentosRepository.findByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        // 삭제 권한 확인
        if (!Objects.equals(mentos.getMember().getMemberSeq(), currentMemberId)) {
            throw new MentosException(NO_AUTHORITY_TO_DELETE);
        }
        mentos.inactivate();
    }

    /* 멘토스 상세 조회 */
    @Override
    public GetMentosDetailResponse getMentosDetail(Long mentosSeq) {
        log.info("[MentosServiceImpl.getMentosDetail]");

        // Mentos 정보 조회
        Mentos mentos = mentosRepository.findByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        // Mentos를 등록한 멘토의 MentoProfile 정보 조회
        MentoProfile mentoProfile = mentoProfileRepository.findByMember_MemberSeq(mentos.getMember().getMemberSeq())
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTO_PROFILE));

        // 리뷰 정보 조회
        List<GetMentosDetailResponse.Review> reviews = reviewRepository.findReviewByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE);

        Integer reviewTotalCnt = reviews.size();

        Double reviewRatingAvg = reviews.stream()
                .mapToInt(GetMentosDetailResponse.Review::getReviewRating)
                .average()
                .orElse(0.0);
        // 소수점 두 번째 자리에서 반올림
        reviewRatingAvg = Math.round(reviewRatingAvg * 100) / 100.0;

        // 최종 응답 DTO 조립
        return GetMentosDetailResponse.builder()
                // Mentos 정보
                .mentosImage(mentos.getMentosImage())
                .mentosTitle(mentos.getMentosTitle())
                .mentosDescription(mentos.getMentosContent())
                .mentosPrice(mentos.getPrice())
                // MentoProfile 정보
                .mentoPostcode(mentoProfile.getMentoPostcode())
                .mentoRoadAddress(mentoProfile.getMentoRoadAddress())
                .mentoBname(mentoProfile.getMentoBname())
                .mentoDetailAddress(mentoProfile.getMentoDetail())
                .startTime(mentoProfile.getStartTime())
                .endTime(mentoProfile.getEndTime())
                .availableDays(mentoProfile.getAvailableDays())
                // Mento 정보
                .mento(GetMentosDetailResponse.MentoDetail.builder()
                        .mentoName(mentoProfile.getMember().getMemberName())
                        .mentoImg(mentoProfile.getMentoProfileImage())
                        .mentoDescription(mentoProfile.getMentoProfileContent())
                        .build())
                // Review 정보
                .reviews(reviews)
                .reviewTotalCnt(reviewTotalCnt)
                .reviewRatingAvg(reviewRatingAvg)
                .build();
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

    /* 멘토스 생성하기 */
    @Override
    @Transactional
    public void createMentos(Long memberSeq, CreateMentosRequest createMentosRequest, MultipartFile imageFile, String idemKey) throws IOException {
        log.info("[MentosServiceImpl.createMentos]");
        try {
            Member member = memberRepository.findByMemberSeqAndStatusAndMemberType(memberSeq, BaseStatus.ACTIVE, MemberType.MENTO)
                    .orElseThrow(() -> new MemberException(NOT_A_MENTO));

            log.info("[MentosServiceImpl.createMentos]....categorySeq==>" + createMentosRequest.getCategorySeq());
            Category category = categoryRepository.findByCategorySeqAndStatus(createMentosRequest.getCategorySeq(), BaseStatus.ACTIVE)
                    .orElseThrow(() -> new CategoryException(CANNOT_FOUND_CATEGORY));

            if (idempotencyService.isDuplicate(idemKey)) {
                log.info("[MentosServiceImpl.createMentos...멘토스 생성 이미 완료...동일한 요청]");
                throw new MentosException(ALREADY_SUCCESS_REQUEST);
            } else {
                // s3 에 업로드된 url 로 db 에 저장하기
                String uploadedMentosImage = s3Uploader.upload(createMentosRequest.getMentosImage());
                Mentos mentos = new Mentos(createMentosRequest.getMentosTitle(), createMentosRequest.getMentosContent(), createMentosRequest.getPrice(), uploadedMentosImage, category, member, BaseStatus.ACTIVE);

                Mentos createdMentos = mentosRepository.save(mentos);
                log.info("[MentosServiceImpl.createMentos...멘토스 생성 완료]");
                idempotencyService.saveKey(idemKey, String.valueOf(createdMentos.getMentosSeq()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Member member = memberRepository.findByMemberSeqAndStatusAndMemberType(memberSeq, BaseStatus.ACTIVE, MemberType.MENTO)
                .orElseThrow(() -> new MemberException(NOT_A_MENTO));

        Category category = categoryRepository.findByCategorySeqAndStatus(createMentosRequest.getCategorySeq(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new CategoryException(CANNOT_FOUND_CATEGORY));

        String uploadedMentosImage = s3Uploader.upload(imageFile);

        // 수정된 생성자 호출
        Mentos mentos = new Mentos(
                createMentosRequest.getMentosTitle(),
                createMentosRequest.getMentosContent(),
                createMentosRequest.getPrice(),
                uploadedMentosImage,
                category,
                member,
                BaseStatus.ACTIVE
        );

        Mentos createdMentos = mentosRepository.save(mentos);
        log.info("[MentosServiceImpl.createMentos...멘토스 생성 완료]");

        idempotencyService.saveKey(idemKey, String.valueOf(createdMentos.getMentosSeq()));
    }

}