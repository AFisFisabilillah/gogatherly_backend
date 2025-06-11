package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "events")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title ;

    @Embedded
    private LocationEvent location;

    private String description;

    private String violation;

    private String banner;

    @Enumerated(value = EnumType.STRING)
    private StatusEvent status;

    @Column(name = "start_event")
    private LocalDateTime startEvent;

    @Column(name = "end_event")
    private LocalDateTime endEvent;

    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

    @ManyToMany
    @JoinTable(
            name = "categories_events",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id" , referencedColumnName = "id")
    )
    private List<Category> categories;

}
