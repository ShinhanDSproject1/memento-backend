package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP를 사용하는 WebSocket 메시지 브로커를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket에 연결하기 위한 엔드포인트를 설정
        registry.addEndpoint("/ws/chat") // 이 주소로 연결
                .setAllowedOriginPatterns("*") // 모든 도메인에서 접속 허용
                .withSockJS(); // SockJS 지원을 활성화하여 오래된 브라우저 호환성 제공
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 /topic으로 시작하는 주소를 구독하는 클라이언트에게 메시지를 전달하도록 설정
        // 클라이언트는 이 주소를 구독(subscribe)하고 있어야 메시지를 받을 수 있음.
        registry.enableSimpleBroker("/topic");

        // 클라이언트가 서버로 메시지를 보낼 때 사용할 주소의 접두사를 설정
        // 예: client.send("/app/chat/send", ...)
        registry.setApplicationDestinationPrefixes("/app");
    }
}