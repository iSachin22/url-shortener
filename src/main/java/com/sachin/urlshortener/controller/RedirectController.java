package com.sachin.urlshortener.controller;

import com.sachin.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.NoSuchElementException;

// Kept at root path (not under /api) so shortened links stay genuinely short,
// e.g. https://short.ly/aB3 instead of https://short.ly/api/aB3
@RestController
public class RedirectController {

    private final UrlShortenerService service;

    public RedirectController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        try {
            String originalUrl = service.resolveOriginalUrl(shortCode);
            service.recordClick(shortCode, request.getRemoteAddr());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl));
            return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
