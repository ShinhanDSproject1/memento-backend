package com.shinhanDS5gi.memento.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatHoldServiceImpl implements SeatHoldService {

    private final StringRedisTemplate redis;

    private static final String PREFIX = "hold:mentos";
    private static final DateTimeFormatter DAY_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private String key(long mentosSeq, LocalDate date, LocalTime time) {
        return "%s:%d:%s:%s".formatted(
                PREFIX, mentosSeq, date.format(DAY_FMT), time.format(TIME_FMT)
        );
    }

    private String dayPattern(long mentosSeq, LocalDate date) {
        // hold:mentos:{seq}:{yyyy-MM-dd}:*
        return "%s:%d:%s:*".formatted(PREFIX, mentosSeq, date.format(DAY_FMT));
    }

    @Override
    public List<LocalTime> findHeldSlots(long mentosSeq, LocalDate date) {
        String pattern = dayPattern(mentosSeq, date);
        List<LocalTime> result = new ArrayList<>();

        RedisConnection conn = redis.getRequiredConnectionFactory().getConnection();
        try (Cursor<byte[]> cur = conn.scan(
                ScanOptions.scanOptions().match(pattern).count(512).build())) {
            while (cur.hasNext()) {
                String k = new String(cur.next(), StandardCharsets.UTF_8);
                String hhmm = k.substring(k.lastIndexOf(':') + 1);
                result.add(LocalTime.parse(hhmm, TIME_FMT));
            }
        }
        return result;
    }

    @Override
    public boolean holdSlot(long mentosSeq, LocalDate date, LocalTime time, String holderId, long ttlSec) {
        String k = key(mentosSeq, date, time);
        Boolean ok = redis.opsForValue().setIfAbsent(
                k, holderId, Duration.ofSeconds(Math.max(1, ttlSec))
        );
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public void releaseSlot(long mentosSeq, LocalDate date, LocalTime time) {
        redis.delete(key(mentosSeq, date, time));
    }

    @Override
    public List<LocalTime> findHeldSlotsExcludingHolder(long mentosSeq, LocalDate date, String holderId) {
        String pattern = dayPattern(mentosSeq, date);
        List<LocalTime> result = new ArrayList<>();

        RedisConnection conn = redis.getRequiredConnectionFactory().getConnection();
        try (Cursor<byte[]> cur = conn.scan(
                ScanOptions.scanOptions().match(pattern).count(512).build())) {
            while (cur.hasNext()) {
                String k = new String(cur.next(), StandardCharsets.UTF_8);
                String hhmm = k.substring(k.lastIndexOf(':') + 1);
                String v = redis.opsForValue().get(k);
                if (v == null || !v.equals(holderId)) {
                    result.add(LocalTime.parse(hhmm, TIME_FMT));
                }
            }
        }
        return result;
    }
}
