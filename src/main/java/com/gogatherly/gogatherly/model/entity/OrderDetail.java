package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;

    private Long total;

    @ManyToOne
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "id"
    )
    private Order order;

    @ManyToOne
    @JoinColumn(
            name = "ticket_id",
            referencedColumnName = "id"
    )
    private Ticket ticket;
}
