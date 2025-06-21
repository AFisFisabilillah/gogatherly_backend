package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_instances")
@Data
public class TicketInstance extends AuditableEntity {
    @Id
    private String id;

    @Column(name = "is_used")
    private Boolean used;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ticket_id",
            referencedColumnName = "id"
    )
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "id"
    )
    private Order order;


}
