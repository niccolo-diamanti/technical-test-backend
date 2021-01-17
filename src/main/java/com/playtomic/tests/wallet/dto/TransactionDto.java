package com.playtomic.tests.wallet.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class TransactionDto {

    @NotNull
    private UUID transactionId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private long requestTimestamp;
}
