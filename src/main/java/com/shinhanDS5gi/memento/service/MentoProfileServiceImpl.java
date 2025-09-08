package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MentoProfileException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.config.S3Uploader;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.mento.CreateMentoProfileRequest;
import com.shinhanDS5gi.memento.dto.mento.UpdateMentoProfileRequest;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mento.MentoProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoProfileServiceImpl implements MentoProfileService {

    private final MentoProfileRepository mentoProfileRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final IdempotencyService idempotencyService;
    private final KakaoMapService kakaoMapService;

    /* 멘토 프로필 생성 */
    @Override
    @Transactional
    public void createMentoProfile(Long memberSeq, CreateMentoProfileRequest requestDto, MultipartFile imageFile, String idemKey) throws IOException {

        if (idempotencyService.isDuplicate(idemKey)) {
            throw new MentoProfileException(ALREADY_SUCCESS_REQUEST);
        }

        /* 회원 가입한 사용자인가? */
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        /* 멘토인가? */
        if (member.getMemberType() != MemberType.MENTO) {
            throw new MentoProfileException(FAILURE, "멘토 회원만 프로필을 생성할 수 있습니다.");
        }

        /* 이미 멘토프로필 생성하진 않았는가? */
        if (mentoProfileRepository.existsByMember_MemberSeq(memberSeq)) {
            throw new MentoProfileException(ALREADY_EXISTS_MENTO_PROFILE);
        }

        /* 카카오 API를 호출하여 주소를 좌표로 변환 */
        double[] coordinates = kakaoMapService.getCoordinates(requestDto.getMentoRoadAddress());
        if (coordinates == null) {
            // 좌표를 찾을 수 없다?
            throw new IllegalArgumentException("유효하지 않은 주소입니다. 좌표를 찾을 수 없습니다.");
        }
        Double longitude = coordinates[0]; // 경도
        Double latitude = coordinates[1];  // 위도

        // S3에 이미지 업로드
        String imageUrl = s3Uploader.upload(imageFile);

        MentoProfile mentoProfile = requestDto.toEntity(member, imageUrl, latitude, longitude);
        MentoProfile savedProfile = mentoProfileRepository.save(mentoProfile);

        // 멱등키 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedProfile.getMentoProfileSeq()));
    }

    /* 멘토 프로필 수정 */
    @Override
    @Transactional
    public void updateMentoProfile(Long memberSeq, UpdateMentoProfileRequest requestDto, MultipartFile imageFile) throws IOException {

        /* 회원 가입한 사용자인가? */
        memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentoProfileException(CANNOT_FOUND_MEMBER));

        /* 수정할 프로필을 조회 */
        MentoProfile mentoProfile = mentoProfileRepository.findByMember_MemberSeq(memberSeq)
                .orElseThrow(() -> new MentoProfileException(CANNOT_FOUND_MENTO_PROFILE));

        String newImageUrl = mentoProfile.getMentoProfileImage();

        if (imageFile != null && !imageFile.isEmpty()) {

            s3Uploader.delete(mentoProfile.getMentoProfileImage());
            newImageUrl = s3Uploader.upload(imageFile);
        }

        /* 카카오 API를 호출하여 주소를 좌표로 변환 */
        double[] coordinates = kakaoMapService.getCoordinates(requestDto.getMentoRoadAddress());
        if (coordinates == null) {
            // 좌표를 찾을 수 없다?
            throw new IllegalArgumentException("유효하지 않은 주소입니다. 좌표를 찾을 수 없습니다.");
        }
        Double longitude = coordinates[0]; // 경도
        Double latitude = coordinates[1];  // 위도

        mentoProfile.update(requestDto, newImageUrl, latitude, longitude);
    }
}
