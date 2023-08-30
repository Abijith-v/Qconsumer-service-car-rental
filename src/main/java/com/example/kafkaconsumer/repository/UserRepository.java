package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    public boolean existsByEmail(String email);

    public Optional<Users> findByEmail(String Email);
}
