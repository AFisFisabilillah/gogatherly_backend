package com.gogatherly.gogatherly.model.repository;

import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndUser_Id(Integer id, Integer idUser);

    @Query(value = "SELECT e.* FROM events e JOIN users u ON e.user_id = u.id  WHERE e.title @@ to_tsquery(:title) AND u.id = :userId",
            nativeQuery = true)
    List<Event> fulltextSearchAndByuserId(@Param("title") String title, @Param("userId") Integer userId );

    List<Event> findAllByUser_id(Integer id);

}
