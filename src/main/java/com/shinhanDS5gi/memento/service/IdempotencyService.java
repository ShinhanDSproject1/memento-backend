package com.shinhanDS5gi.memento.service;

public interface IdempotencyService {
    boolean isDuplicate(String key);

    void saveKey(String key, String value);

    String getSavedResponse(String key);
}
