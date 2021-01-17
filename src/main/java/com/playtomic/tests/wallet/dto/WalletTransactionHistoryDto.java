package com.playtomic.tests.wallet.dto;

import com.playtomic.tests.wallet.data.model.WalletTransactionHistory;
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
public class WalletTransactionHistoryDto {

    private long id;
    private BigDecimal quantity;
    private String type;
    private Long timestamp;
    private Long requestTimestamp;
    private long wallet;
    private UUID transactionId;

    public WalletTransactionHistoryDto(WalletTransactionHistory walletTransactionHistory) {
        if (walletTransactionHistory != null) {
            this.id = walletTransactionHistory.getId();
            this.quantity = walletTransactionHistory.getQuantity();
            this.type = walletTransactionHistory.getType();
            this.timestamp = walletTransactionHistory.getTimestamp().toEpochMilli();
            this.requestTimestamp = walletTransactionHistory.getRequestTimestamp().toEpochMilli();
            this.wallet = walletTransactionHistory.getWalletId();
            this.transactionId = walletTransactionHistory.getTransactionId();
        }
    }
}
