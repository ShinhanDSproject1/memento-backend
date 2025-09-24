package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // 모든 메시지에 대한 접근을 일단 허용하도록 변경
        // 실제 인증 처리는 StompHandler에서 담당
        messages.anyMessage().permitAll();
    }

    // CSRF 토큰 검사를 비활성화
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
