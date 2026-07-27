package com.example.pyksel.infrastructure.persistence.pixel;

import com.example.pyksel.infrastructure.persistence.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "pixels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pixel {

    @EmbeddedId
    private PixelId id;

    @Column(nullable = false, length = 7)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painted_by")
    private User paintedBy;

    @Column(nullable = false)
    private Instant paintedAt;
}