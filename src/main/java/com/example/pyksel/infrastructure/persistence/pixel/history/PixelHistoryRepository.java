package com.example.pyksel.infrastructure.persistence.pixel.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PixelHistoryRepository extends JpaRepository<PixelHistory, Long> {

    List<PixelHistory> findByXAndYOrderByPaintedAtDesc(short x, short y);
}