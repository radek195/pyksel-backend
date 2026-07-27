package com.example.pyksel.infrastructure.persistence.pixel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PixelRepository extends JpaRepository<Pixel, PixelId> {

    @Query("SELECT p FROM Pixel p WHERE p.id.x = :x AND p.id.y = :y")
    Optional<Pixel> findByXAndY(@Param("x") short x, @Param("y") short y);
}
