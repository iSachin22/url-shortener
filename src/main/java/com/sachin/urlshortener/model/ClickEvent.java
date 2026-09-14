package com.sachin.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clicks")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long urlId;

    @Column(nullable = false)
    private LocalDateTime clickedAt = LocalDateTime.now();

    private String sourceIp;

    public ClickEvent() {}

    public ClickEvent(Long urlId, String sourceIp) {
        this.urlId = urlId;
        this.sourceIp = sourceIp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUrlId() { return urlId; }
    public void setUrlId(Long urlId) { this.urlId = urlId; }

    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
}
