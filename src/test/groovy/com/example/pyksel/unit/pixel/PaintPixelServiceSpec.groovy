package com.example.pyksel.unit.pixel

import com.example.pyksel.domian.pixel.PaintPixelService
import com.example.pyksel.infrastructure.persistence.pixel.Pixel
import com.example.pyksel.infrastructure.persistence.pixel.PixelId
import com.example.pyksel.infrastructure.persistence.pixel.PixelRepository
import com.example.pyksel.infrastructure.persistence.pixel.history.PixelHistoryRepository
import com.example.pyksel.infrastructure.persistence.user.User
import com.example.pyksel.infrastructure.persistence.user.UserRepository
import com.example.pyksel.infrastructure.persistence.user.cooldown.UserCooldown
import com.example.pyksel.infrastructure.persistence.user.cooldown.UserCooldownRepository
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class PaintPixelServiceSpec extends Specification {

    private static final Instant NOW = Instant.parse("2026-07-27T12:00:00Z")

    def userRepository = Mock(UserRepository)
    def userCooldownRepository = Mock(UserCooldownRepository)
    def pixelRepository = Mock(PixelRepository)
    def pixelHistoryRepository = Mock(PixelHistoryRepository)
    def service = new PaintPixelService(
        userRepository,
        userCooldownRepository,
        pixelRepository,
        pixelHistoryRepository,
        Clock.fixed(NOW, ZoneOffset.UTC)
    )

    def "should paint pixel, create history entry and cooldown"() {
        given:
            def user = User.builder().id(UUID.randomUUID()).build()
            def pixelId = new PixelId((short) 10, (short) 20)

        when:
            service.paint(user.id, (short) 10, (short) 20, "#FF0000")

        then:
            1 * userRepository.findByIdForUpdate(user.id) >> Optional.of(user)
            1 * userCooldownRepository.findByUser(user) >> Optional.empty()
            1 * pixelRepository.findById(pixelId) >> Optional.empty()
            1 * pixelRepository.save({ Pixel pixel ->
                pixel.id == pixelId &&
                    pixel.color == "#FF0000" &&
                    pixel.paintedBy == user &&
                    pixel.paintedAt == NOW
            })
            1 * pixelHistoryRepository.save({ history ->
                history.x == (short) 10 &&
                    history.y == (short) 20 &&
                    history.color == "#FF0000" &&
                    history.paintedBy == user &&
                    history.paintedAt == NOW
            })
            1 * userCooldownRepository.save({ UserCooldown cooldown ->
                cooldown.user == user && cooldown.lastPaintedAt == NOW
            })
    }

    def "should update an existing pixel if cooldown is expired"() {
        given:
            def user = User.builder().id(UUID.randomUUID()).build()
            def pixelId = new PixelId((short) 10, (short) 20)
            def existingPixel = Pixel.builder().id(pixelId).color("#FFFFFF").paintedAt(NOW.minusSeconds(7200)).build()
            def cooldown = UserCooldown.builder().user(user).lastPaintedAt(NOW.minusSeconds(3600)).build()

        when:
            service.paint(user.id, (short) 10, (short) 20, "#00FF00")

        then:
            1 * userRepository.findByIdForUpdate(user.id) >> Optional.of(user)
            1 * userCooldownRepository.findByUser(user) >> Optional.of(cooldown)
            1 * pixelRepository.findById(pixelId) >> Optional.of(existingPixel)
            1 * pixelRepository.save(existingPixel)
            1 * pixelHistoryRepository.save(_)
            1 * userCooldownRepository.save(cooldown)
            existingPixel.color == "#00FF00"
            existingPixel.paintedBy == user
            existingPixel.paintedAt == NOW
            cooldown.lastPaintedAt == NOW
    }

    def "should reject painting if cooldown is not expired"() {
        given:
            def user = User.builder().id(UUID.randomUUID()).build()
            def cooldown = UserCooldown.builder().user(user).lastPaintedAt(NOW.minusSeconds(3599)).build()

        when:
            service.paint(user.id, (short) 10, (short) 20, "#FF0000")

        then:
            1 * userRepository.findByIdForUpdate(user.id) >> Optional.of(user)
            1 * userCooldownRepository.findByUser(user) >> Optional.of(cooldown)
            0 * pixelRepository._
            0 * pixelHistoryRepository._
            0 * userCooldownRepository.save(_)
            def exception = thrown(ResponseStatusException)
            exception.statusCode.value() == 429
    }
}
