package com.example.pyksel.functional

import com.example.pyksel.infrastructure.auth.AuthResponse
import com.example.pyksel.infrastructure.auth.LoginRequest
import com.example.pyksel.infrastructure.auth.RegisterRequest
import com.example.pyksel.infrastructure.persistence.pixel.PixelId
import com.example.pyksel.infrastructure.persistence.pixel.PixelRepository
import com.example.pyksel.infrastructure.persistence.pixel.history.PixelHistoryRepository
import com.example.pyksel.infrastructure.persistence.user.UserRepository
import com.example.pyksel.infrastructure.persistence.user.cooldown.UserCooldownRepository
import com.example.pyksel.interfaces.pixel.PaintPixelRequest
import com.example.pyksel.common.specs.PostgresContainerSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationFT extends PostgresContainerSpec {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    UserRepository userRepository

    @Autowired
    PixelRepository pixelRepository

    @Autowired
    PixelHistoryRepository pixelHistoryRepository

    @Autowired
    UserCooldownRepository userCooldownRepository

    def "should register, log in and paint a pixel"() {
        given:
            def username = "painter-${UUID.randomUUID().toString()[0..7]}"
            def registrationRequest = new RegisterRequest(username, "safe-password", "${username}@example.com")

        when: "a user registers"
            def registration = restTemplate.postForEntity("/api/auth/register", registrationRequest, AuthResponse)

        then: "the API confirms creation and the user is persisted"
            registration.statusCode == HttpStatus.CREATED
            registration.body.token
            def registeredUser = userRepository.findByUsername(username).orElseThrow()
            registeredUser.username == username
            registeredUser.password != "safe-password"

        when: "the registered user logs in"
            def login = restTemplate.postForEntity(
                "/api/auth/login",
                new LoginRequest(username, "safe-password"),
                AuthResponse
            )

        then: "the API returns a JWT"
            login.statusCode == HttpStatus.OK
            login.body.token

        when: "the authenticated user paints a pixel"
            def headers = new HttpHeaders()
            headers.setBearerAuth(login.body.token)
            def paintResponse = restTemplate.postForEntity(
                "/api/pixels",
                new HttpEntity<>(new PaintPixelRequest((short) 10, (short) 20, "#FF0000"), headers),
                Void
            )

        then: "the pixel and related records are persisted"
            paintResponse.statusCode == HttpStatus.NO_CONTENT

            def pixel = pixelRepository.findById(new PixelId((short) 10, (short) 20)).orElseThrow()
            pixel.color == "#FF0000"
            pixel.paintedBy.id == registeredUser.id
            pixel.paintedAt

            def history = pixelHistoryRepository.findByXAndYOrderByPaintedAtDesc((short) 10, (short) 20)
            history.size() == 1
            history.first().color == "#FF0000"
            history.first().paintedBy.id == registeredUser.id

            def cooldown = userCooldownRepository.findById(registeredUser.id).orElseThrow()
            cooldown.lastPaintedAt
    }
}
