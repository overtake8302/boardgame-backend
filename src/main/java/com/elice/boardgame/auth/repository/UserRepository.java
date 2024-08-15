package com.elice.boardgame.auth.repository;

import com.elice.boardgame.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    boolean existsByUsername(String username);

    User findByUsername(String username);

    User findByUsernameAndUserStateIsNull(String username);


}
