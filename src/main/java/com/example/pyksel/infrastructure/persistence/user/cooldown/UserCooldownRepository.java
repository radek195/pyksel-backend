package com.example.pyksel.infrastructure.persistence.user.cooldown;

import com.example.pyksel.infrastructure.persistence.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCooldownRepository extends JpaRepository<UserCooldown, UUID> {

    Optional<UserCooldown> findByUser(User user);
}
