package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
}
