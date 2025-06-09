package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@DiscriminatorValue("ROLE_EVENT_MANAGER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManager extends User{

    @OneToMany(mappedBy = "user")
    private List<Event> events;

    @Column(name = "description_em")
    private String description;
}
