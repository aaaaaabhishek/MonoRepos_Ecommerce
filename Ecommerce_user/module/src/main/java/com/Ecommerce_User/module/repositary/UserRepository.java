package com.Ecommerce_User.module.repositary;

import com.Ecommerce_User.module.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean findByUsername(String user_name);

    boolean findByEmail(String user_name);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}
