package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}
