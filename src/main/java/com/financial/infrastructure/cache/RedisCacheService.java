package com.financial.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    public <T> void set(String key, T value, Duration ttl) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl);
            log.debug("Cached object with key: {}", key);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to cache: {}", e.getMessage());
        }
    }
    
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            String jsonValue = redisTemplate.opsForValue().get(key);
            if (jsonValue != null) {
                T value = objectMapper.readValue(jsonValue, type);
                log.debug("Retrieved cached object with key: {}", key);
                return Optional.of(value);
            }
        } catch (Exception e) {
            log.error("Error deserializing cached object: {}", e.getMessage());
        }
        return Optional.empty();
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Deleted cache key: {}", key);
    }
    
    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }
    
    public void setWithExpire(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }
}