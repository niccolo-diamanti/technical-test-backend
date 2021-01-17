package com.playtomic.tests.wallet.data.repository;

import com.playtomic.tests.wallet.data.model.WalletTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionHistoryRepository extends JpaRepository<WalletTransactionHistory, Long> {

    WalletTransactionHistory findFirstByWalletIdOrderByTimestampDesc(long walletId);
}
