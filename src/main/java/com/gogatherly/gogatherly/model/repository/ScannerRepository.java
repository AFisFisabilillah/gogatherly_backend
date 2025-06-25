package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.Scanner;
import com.gogatherly.gogatherly.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScannerRepository extends JpaRepository<Scanner, Integer> {
  Optional<Scanner> findByUsername(String username);

  Optional<Scanner> findByEvent_User(User user);

    List<Scanner> findByEvent_UserAndEvent_Id(User user, Integer id);

  List<Scanner> findByEvent(Event event);

  Optional<Scanner> findByIdAndEvent_UserAndEvent_Id(Integer scannerId,User user, Integer eventId);

  Optional<Scanner> findByEventAndId(Event event, Integer id);

}