package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ROLE_EVENT_MANAGER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManager extends User{

    @Column(name = "description_em")
    private String description;
}
