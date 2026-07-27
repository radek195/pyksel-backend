package com.example.pyksel.infrastructure.persistence.pixel.history;

import com.example.pyksel.infrastructure.persistence.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "pixel_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PixelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Short x;

    @Column(nullable = false)
    private Short y;

    @Column(nullable = false, length = 7)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painted_by")
    private User paintedBy;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant paintedAt;
}