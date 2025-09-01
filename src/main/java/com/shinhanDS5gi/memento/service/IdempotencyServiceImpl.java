package com.shinhanDS5gi.memento.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean isDuplicate(String key) {
        log.info("[IdempotencyService.isDuplicate]");
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void saveKey(String key, String value) {
        log.info("[IdempotencyService.saveKey]");
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.SECONDS); // TTL : 30초
    }

    @Override
    public String getSavedResponse(String key) {
        log.info("[IdempotencyService.getSavedResponse]");
        return redisTemplate.opsForValue().get(key);
    }
}
