package com.shinhanDS5gi.memento.common.exception_handler;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.BaseException;
import com.shinhanDS5gi.memento.common.response.BaseErrorResponse;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.common.response.status.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AuthException 처리 (특수 로직)
     * 클라이언트에게는 항상 '로그인 실패'로 통합 응답합니다.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<BaseErrorResponse> handleAuthException(AuthException e) {
        log.warn("[AuthException] status={}, msg={}", e.getExceptionStatus(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(BaseExceptionResponseStatus.CANNOT_LOGIN));
    }

    /**
     * AuthException을 제외한 모든 커스텀 예외(BaseException) 처리
     * Enum에 정의된 실제 HTTP 상태 코드를 동적으로 사용합니다.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseErrorResponse> handleBaseException(BaseException e) {
        log.error("[BaseException] status={}, msg={}", e.getExceptionStatus(), e.getMessage());
        ResponseStatus status = e.getExceptionStatus();
        return ResponseEntity.status(status.getStatus())
                .body(new BaseErrorResponse(status));
    }

    /**
     * 처리하지 못한 모든 예외 처리 (최종 안전망)
     * NullPointerException 등 예측하지 못한 모든 서버 오류를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> handleUnexpectedException(Exception e) {
        log.error("[UnexpectedException]", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseErrorResponse(BaseExceptionResponseStatus.FAILURE));
    }
}