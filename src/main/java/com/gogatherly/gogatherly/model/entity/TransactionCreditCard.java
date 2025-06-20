package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("credit_card")
@Entity
public class TransactionCreditCard extends Transaction{
    @Column(name = "masked_card")
    private String maskedCard;
}
