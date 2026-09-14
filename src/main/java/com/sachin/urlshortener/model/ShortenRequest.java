package com.sachin.urlshortener.model;

import jakarta.validation.constraints.NotBlank;

public class ShortenRequest {

    @NotBlank(message = "originalUrl must not be blank")
    private String originalUrl;

    public ShortenRequest() {}

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
}
