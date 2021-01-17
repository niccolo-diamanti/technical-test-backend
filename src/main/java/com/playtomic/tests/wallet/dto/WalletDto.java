package com.playtomic.tests.wallet.dto;

import com.playtomic.tests.wallet.data.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletDto {

    private Long id;
    private BigDecimal balance;
    //Assume that all transactions can be too many and will be mapped in another DTO (List<WalletTransactionHistoryDto>)
    //Last transaction can be useful for a quick view or validation
    private WalletTransactionHistoryDto lastTransaction;
    private UUID userId;

    public WalletDto(Wallet wallet) {
        this.id = wallet.getId();
        this.balance = wallet.getBalance();
        this.userId = wallet.getUserId();
    }
}
