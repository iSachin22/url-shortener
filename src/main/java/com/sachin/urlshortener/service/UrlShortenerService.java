package com.sachin.urlshortener.service;

import com.sachin.urlshortener.model.ClickEvent;
import com.sachin.urlshortener.model.UrlMapping;
import com.sachin.urlshortener.repository.ClickEventRepository;
import com.sachin.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String CACHE_PREFIX = "shorturl:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository,
                                ClickEventRepository clickEventRepository,
                                RedisTemplate<String, String> redisTemplate) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventRepository = clickEventRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Creates a short URL for the given original URL.
     * The short code is a Base62 encoding of the row's auto-increment id,
     * so codes are short, unique-by-construction, and collision-free
     * without needing a separate uniqueness check loop.
     */
    public String createShortUrl(String originalUrl) {
        UrlMapping mapping = new UrlMapping(originalUrl, "PENDING");
        mapping = urlMappingRepository.save(mapping); // id is generated here
        String code = encodeBase62(mapping.getId());
        mapping.setShortCode(code);
        urlMappingRepository.save(mapping);
        return baseUrl + "/" + code;
    }

    public String resolveOriginalUrl(String shortCode) {
        // 1. Try cache first
        String cached = redisTemplate.opsForValue().get(CACHE_PREFIX + shortCode);
        if (cached != null) {
            return cached;
        }

        // 2. Fall back to DB
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short code not found: " + shortCode));

        // 3. Populate cache for next time
        redisTemplate.opsForValue().set(CACHE_PREFIX + shortCode, mapping.getOriginalUrl(), CACHE_TTL);

        return mapping.getOriginalUrl();
    }

    public void recordClick(String shortCode, String sourceIp) {
        Optional<UrlMapping> mapping = urlMappingRepository.findByShortCode(shortCode);
        mapping.ifPresent(m -> clickEventRepository.save(new ClickEvent(m.getId(), sourceIp)));
    }

    public long getClickCount(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short code not found: " + shortCode));
        return clickEventRepository.countByUrlId(mapping.getId());
    }

    private String encodeBase62(long id) {
        StringBuilder sb = new StringBuilder();
        long value = id;
        if (value == 0) return "0";
        while (value > 0) {
            sb.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}
