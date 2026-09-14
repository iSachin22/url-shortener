package com.sachin.urlshortener.controller;

import com.sachin.urlshortener.model.ShortenRequest;
import com.sachin.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortUrl = service.createShortUrl(request.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("shortUrl", shortUrl));
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable String shortCode) {
        try {
            long clicks = service.getClickCount(shortCode);
            return ResponseEntity.ok(Map.of("shortCode", shortCode, "totalClicks", clicks));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
