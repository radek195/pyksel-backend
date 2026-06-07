package com.example.pyksel.functional

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ApplicationFT extends Specification {

    def 'should start application'() {
        expect:
            true
    }

}
