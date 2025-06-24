package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Scanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScannerRepository extends JpaRepository<Scanner, Integer> {
  Optional<Scanner> findByUsername(String username);

}