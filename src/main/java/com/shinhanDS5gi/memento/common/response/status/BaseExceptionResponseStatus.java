package com.shinhanDS5gi.memento.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus{

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000,HttpStatus.OK.value(), "요청에 성공하였습니다."),
    FAILURE(2000, HttpStatus.BAD_REQUEST.value(), "요청에 실패하였습니다."),

    /**
     * Mentos 관련 2500대
     */
    CANNOT_FOUND_MENTOS(2500, HttpStatus.BAD_REQUEST.value(), "해당 멘토스 게시글을 찾을 수 없습니다."),
    NO_AUTHORITY_TO_UPDATE(2501, HttpStatus.FORBIDDEN.value(), "게시글을 수정할 권한이 없습니다."),
    NO_AUTHORITY_TO_DELETE(2502, HttpStatus.FORBIDDEN.value(), "게시글을 삭제할 권한이 없습니다."),
    NO_MENTOS_FOUND_FOR_MEMBER(2503, HttpStatus.NOT_FOUND.value(), "멘토가 작성한 멘토스 내역이 존재하지 않습니다."),
    NO_REVIEWS_FOUND_FOR_MENTO(2504, HttpStatus.NOT_FOUND.value(), "요청하신 멘토의 멘토스 리뷰는 존재하지 않습니다."),

    /**
     * Token 관련 3000대
     */
    INVALID_TOKEN(3000, HttpStatus.OK.value(), "유효하지 않은 토큰입니다."),

    /**
     * Member 관련 4000대
     */
    CANNOT_FOUND_MEMBER(4000,HttpStatus.NOT_FOUND.value(),"해당 사용자를 찾을 수 없습니다."),
    NOT_A_MENTO(4001, HttpStatus.FORBIDDEN.value(), "멘토 회원만 접근할 수 있는 기능입니다."),
    PASSWORD_MISMATCH(4001, HttpStatus.BAD_REQUEST.value(), "현재 비밀번호가 일치하지 않습니다."),
    NEW_PASSWORD_CONFIRM_MISMATCH(4002, HttpStatus.BAD_REQUEST.value(), "새 비밀번호와 비밀번호 확인 정보가 일치하지 않습니다."),

    /**
     * Auth 관련 5000대
     */
    CANNOT_SIGNUP(5000,HttpStatus.BAD_REQUEST.value(),"회원가입에 실패했습니다."),
    INVALID_MEMBER_ID(5001, HttpStatus.BAD_REQUEST.value(), "아이디가 올바르지 않습니다."),
    INVALID_PASSWORD(5002, HttpStatus.BAD_REQUEST.value(), "비밀번호가 올바르지 않습니다."),
    CANNOT_LOGIN(5003,HttpStatus.BAD_REQUEST.value(),"로그인에 실패했습니다."),

    /**
     * Report 관련 8000대
     */
    CANNOT_FOUND_REPORT(8000, HttpStatus.NOT_FOUND.value(), "해당 ID의 신고 내역을 찾을 수 없습니다."),

    /**
     * MentoProfile 관련 9000대
     */
    ALREADY_EXISTS_MENTO_PROFILE(9000, HttpStatus.BAD_REQUEST.value(), "이미 멘토 프로필이 존재합니다."),
    CANNOT_FOUND_MENTO_PROFILE(9001,HttpStatus.NOT_FOUND.value(), "해당 SEQ의 멘토 프로필 내역을 찾을 수 없습니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
