package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.dto.TransactionDto;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.service.error.PaymentServiceException;

import java.util.Optional;
import java.util.UUID;

public interface WalletService {

    Optional<WalletDto> createWallet(WalletDto walletDto);

    Optional<WalletDto> findWalletById(long id);

    Optional<WalletDto> findWalletByUserId(UUID userId);

    Optional<WalletDto> pay(long walletId, TransactionDto transactionDto);

    Optional<WalletDto> recharge(long walletId, TransactionDto transactionDto) throws PaymentServiceException;
}
