package com.example.pyksel.infrastructure.persistence.user.cooldown;

import com.example.pyksel.infrastructure.persistence.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_cooldowns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCooldown {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant lastPaintedAt;
}