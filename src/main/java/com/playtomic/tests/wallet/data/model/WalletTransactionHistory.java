package com.playtomic.tests.wallet.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletTransactionHistory {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "SEQ_WALLET_HISTORY_ID")
    private long id;

    @Column(nullable = false, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Instant requestTimestamp;

    @Column(nullable = false)
    private long walletId;

    @Column(unique = true)
    private UUID transactionId;
}
