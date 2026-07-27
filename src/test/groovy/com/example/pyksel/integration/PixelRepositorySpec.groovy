package com.example.pyksel.integration

import com.example.pyksel.common.providers.PixelProvider
import com.example.pyksel.common.providers.UserProvider
import com.example.pyksel.common.specs.PostgresSpec
import com.example.pyksel.infrastructure.persistence.pixel.PixelId
import com.example.pyksel.infrastructure.persistence.pixel.PixelRepository
import com.example.pyksel.infrastructure.persistence.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

class PixelRepositorySpec extends PostgresSpec implements PixelProvider, UserProvider {

    @Autowired
    UserRepository userRepository

    @Subject
    @Autowired
    PixelRepository pixelRepository

    def "should save and find pixel"() {
        given:
            def pixel = getPixel()

        when:
            pixelRepository.save(pixel)
            def optional = pixelRepository.findByXAndY((short) 10, (short) 20)

        then:
            def found = optional.get()
            found.color == "#FF0000"
    }

    def "should save and find single pixel painted by one user"() {
        given:
            def user = getUser("Jaffra")
            def pixel = getPixel(user)

        when:
            userRepository.save(user)
            pixelRepository.save(pixel)
            def optional = pixelRepository.findByXAndY((short) 10, (short) 20)

        then:
            def found = optional.get()
            found.color == "#FF0000"
            found.paintedBy.username == "Jaffra"
    }

    def "should save and find multiple pixels painted by one user"() {
        given:
            def user = getUser("Jaffra")
            def pixel1 = getPixel(user)
            def pixel2 = getPixel(user, new PixelId((short) 11, (short) 11))

        when:
            userRepository.save(user)
            pixelRepository.save(pixel1)
            pixelRepository.save(pixel2)
            def optional1 = pixelRepository.findByXAndY((short) 10, (short) 20)
            def optional2 = pixelRepository.findByXAndY((short) 11, (short) 11)

        then:
            def foundPixel1 = optional1.get()
            foundPixel1.paintedBy.username == "Jaffra"

        and:
            def foundPixel2 = optional2.get()
            foundPixel2.paintedBy.username == "Jaffra"
    }


    def "should return empty for non existent pixel"() {
        when:
            def found = pixelRepository.findByXAndY((short) 99, (short) 99)

        then:
            found.isEmpty()
    }
}