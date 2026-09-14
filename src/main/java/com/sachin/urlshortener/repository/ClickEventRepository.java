package com.sachin.urlshortener.repository;

import com.sachin.urlshortener.model.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlId(Long urlId);
    long countByUrlId(Long urlId);
}
