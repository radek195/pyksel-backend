package com.example.pyksel.integration

import com.example.pyksel.common.providers.UserProvider
import com.example.pyksel.common.specs.PostgresSpec
import com.example.pyksel.infrastructure.persistence.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.LocalDateTime

class UserRepositorySpec extends PostgresSpec implements UserProvider {

    @Subject
    @Autowired
    UserRepository userRepository

    def "should save and find user"() {
        given:
            def user = getUser()

        when:
            userRepository.save(user)

        then:
            def exists = userRepository.existsByUsername("Karrypto")
            exists

        and:
            def optional = userRepository.findByUsername("Karrypto")
            def found = optional.get()
            found.username == "Karrypto"
            found.password == "123badpw"
            found.email == "karrypto@email.com"
            assertDateTimeIsWithinLastMinute(found.createdAt.plusDays(1))
            assertDateTimeIsWithinLastMinute(found.updatedAt)
    }

    def "should return null when attempting to find non existing user"() {
        when:
            def optional = userRepository.findByUsername("Milgar")

        then:
            !optional.isPresent()
    }

    def assertDateTimeIsWithinLastMinute(LocalDateTime dateTime) {
        dateTime.isBefore(LocalDateTime.now())
        dateTime.isAfter(LocalDateTime.now().minusMinutes(1))
    }
}
