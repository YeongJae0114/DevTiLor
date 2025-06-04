package com.toy.devtilor.devtilor.domain.user.repository;

import com.toy.devtilor.devtilor.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Optional<User> findByEmail(String email);
}