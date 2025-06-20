package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DiscriminatorValue("bank")
@Entity
public class TransactionBank extends Transaction {
    @Column(name = "va_number")
    private String VANumber;


    private String bank;
}
