package com.playtomic.tests.wallet.data.model;

import com.playtomic.tests.wallet.dto.WalletDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Wallet {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "SEQ_WALLET_ID")
    private Long id;

    //Assume that one user can have only one wallet
    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, scale = 2)
    private BigDecimal balance;

    public Wallet(WalletDto walletDto) {
        this.id = walletDto.getId();
        this.balance = walletDto.getBalance();
        this.userId = walletDto.getUserId();
    }
}
