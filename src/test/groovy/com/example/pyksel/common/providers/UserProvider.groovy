package com.example.pyksel.common.providers

import com.example.pyksel.domian.user.Role
import com.example.pyksel.infrastructure.persistence.user.User

trait UserProvider {
    def getUser(
        def name = "Karrypto",
        def password = "123badpw",
        def email = "karrypto@email.com",
        def role = Role.USER
    ) {
        User.builder()
            .username(name)
            .password(password)
            .email(email)
            .role(role)
            .build()
    }
}