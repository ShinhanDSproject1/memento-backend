package com.shinhanDS5gi.memento.common.exception_handler;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.response.BaseErrorResponse;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Priority(0)
@Order(0)
public class AuthExceptionControllerAdvice {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<BaseErrorResponse> handleAuth(AuthException e) {

        // 내부 사유(INVALID_MEMBER_ID, INVALID_PASSWORD 등)는 로그로만 확인가능
        log.warn("[AuthException] status={}, msg={}", e.getExceptionStatus(), e.getMessage());

        // 클라이언트 응답은 항상 '로그인 실패'로 통합
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseErrorResponse(
                        BaseExceptionResponseStatus.CANNOT_LOGIN,
                        BaseExceptionResponseStatus.CANNOT_LOGIN.getMessage()
                ));
    }
}
