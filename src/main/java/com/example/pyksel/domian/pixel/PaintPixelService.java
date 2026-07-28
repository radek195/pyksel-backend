package com.example.pyksel.domian.pixel;

import com.example.pyksel.infrastructure.persistence.pixel.Pixel;
import com.example.pyksel.infrastructure.persistence.pixel.PixelId;
import com.example.pyksel.infrastructure.persistence.pixel.PixelRepository;
import com.example.pyksel.infrastructure.persistence.pixel.history.PixelHistory;
import com.example.pyksel.infrastructure.persistence.pixel.history.PixelHistoryRepository;
import com.example.pyksel.infrastructure.persistence.user.User;
import com.example.pyksel.infrastructure.persistence.user.UserRepository;
import com.example.pyksel.infrastructure.persistence.user.cooldown.UserCooldown;
import com.example.pyksel.infrastructure.persistence.user.cooldown.UserCooldownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaintPixelService {

    private static final Duration COOLDOWN = Duration.ofHours(1);

    private final UserRepository userRepository;
    private final UserCooldownRepository userCooldownRepository;
    private final PixelRepository pixelRepository;
    private final PixelHistoryRepository pixelHistoryRepository;
    private final Clock clock;

    @Transactional
    public void paint(UUID userId, short x, short y, String color) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Instant now = clock.instant();
        UserCooldown cooldown = userCooldownRepository.findByUser(user).orElse(null);
        if (cooldown != null && cooldown.getLastPaintedAt().plus(COOLDOWN).isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "You can paint another pixel in one hour");
        }

        PixelId pixelId = new PixelId(x, y);
        Pixel pixel = pixelRepository.findById(pixelId)
                .orElseGet(() -> Pixel.builder().id(pixelId).build());
        pixel.setColor(color);
        pixel.setPaintedBy(user);
        pixel.setPaintedAt(now);
        pixelRepository.save(pixel);

        pixelHistoryRepository.save(PixelHistory.builder()
                .x(x)
                .y(y)
                .color(color)
                .paintedBy(user)
                .paintedAt(now)
                .build());

        if (cooldown == null) {
            cooldown = UserCooldown.builder().user(user).build();
        }
        cooldown.setLastPaintedAt(now);
        userCooldownRepository.save(cooldown);
    }
}
