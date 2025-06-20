package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private  String id;

    @Column(name = "gross_amount")
    private Long grossAmount;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "fraud_status")
    private String fraudStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "order_id",referencedColumnName = "id")
    private Order order;

}
