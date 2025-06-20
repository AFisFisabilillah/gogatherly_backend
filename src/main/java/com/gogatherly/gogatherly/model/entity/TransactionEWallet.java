package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DiscriminatorValue("ewallet")
@Entity
public class TransactionEWallet extends Transaction {
    @Column(name = "ewallet_name")
    private String EWalletName;
}
