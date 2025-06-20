package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DiscriminatorValue("qris")
@Entity
@Data
public class TransactionQris extends Transaction {
    private String issuer;
    private String acquirer;
}
