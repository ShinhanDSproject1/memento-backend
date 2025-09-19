package com.shinhanDS5gi.memento.config;

import lombok.RequiredArgsConstructor; // RequiredArgsConstructor import 추가
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration; // ChannelRegistration import 추가
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // StompHandler 주입

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    // StompHandler를 WebSocket 인터셉터로 등록하는 메서드
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 클라이언트가 서버로 메시지를 보내기 전에 StompHandler가 먼저 가로채서 처리
        registration.interceptors(stompHandler);
    }
}