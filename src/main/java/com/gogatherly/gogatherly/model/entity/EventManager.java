package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ROLE_EVENT_MANAGER")
public class EventManager extends User{

    @Column(name = "description_em")
    private String description;
}
