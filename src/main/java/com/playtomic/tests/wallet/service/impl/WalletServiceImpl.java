package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.data.model.Wallet;
import com.playtomic.tests.wallet.data.model.WalletTransactionHistory;
import com.playtomic.tests.wallet.data.repository.WalletRepository;
import com.playtomic.tests.wallet.data.repository.WalletTransactionHistoryRepository;
import com.playtomic.tests.wallet.dto.TransactionDto;
import com.playtomic.tests.wallet.dto.TransactionType;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.dto.WalletTransactionHistoryDto;
import com.playtomic.tests.wallet.service.PaymentService;
import com.playtomic.tests.wallet.service.WalletService;
import com.playtomic.tests.wallet.service.error.InsufficientBalanceException;
import com.playtomic.tests.wallet.service.error.PaymentServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class WalletServiceInterface implements WalletService {

    private final WalletRepository _walletRepository;
    private final WalletTransactionHistoryRepository _walletTransactionHistoryRepository;
    private final PaymentService _paymentService;

    @Override
    public Optional<WalletDto> createWallet(WalletDto walletDto) {
        Optional<WalletDto> optionalWallet = Optional.empty();
        try {
            Wallet savedWallet = _walletRepository.save(new Wallet(walletDto));
            optionalWallet = Optional.of(new WalletDto(savedWallet));
        } catch (Exception exception) {
            log.error("Error creating wallet [userId: {}; exception: {}]", walletDto.getUserId(), exception);
        }
        return optionalWallet;
    }

    @Override
    public Optional<WalletDto> findWalletById(long id) {
        Optional<Wallet> wallet = _walletRepository.findById(id);
        if (wallet.isPresent()) {
            WalletDto walletDto = new WalletDto(wallet.get());
            walletDto.setLastTransaction(new WalletTransactionHistoryDto(getLastWalletTransactionHistory(wallet.get().getId())));
            return Optional.of(walletDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<WalletDto> findWalletByUserId(UUID userId) {
        Optional<Wallet> wallet = _walletRepository.findByUserId(userId);
        if (wallet.isPresent()) {
            WalletDto walletDto = new WalletDto(wallet.get());
            walletDto.setLastTransaction(new WalletTransactionHistoryDto(getLastWalletTransactionHistory(wallet.get().getId())));
            return Optional.of(walletDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<WalletDto> pay(long walletId, TransactionDto transactionDto) {
        Optional<Wallet> wallet = _walletRepository.findById(walletId);

        //Check if wallet exist
        if (wallet.isPresent()) {
            Wallet walletDb = wallet.get();

            //Check if current balance is enough to pay the amount
            if (walletDb.getBalance().compareTo(transactionDto.getAmount()) >= 0) {

                //Update the wallet balance
                walletDb = updateWalletBalance(transactionDto, walletDb, false);

                //Add the transaction to transactions history
                WalletTransactionHistory transactionHistory = addTransactionHistory(walletId, transactionDto, TransactionType.TYPE_PAYMENT);

                //Return the walletDto
                WalletDto walletDto = new WalletDto(walletDb);
                walletDto.setLastTransaction(new WalletTransactionHistoryDto(transactionHistory));
                return Optional.of(walletDto);
            } else {
                throw new InsufficientBalanceException("Insufficient balance. Balance available: " + walletDb.getBalance() + ", amount to pay: " + transactionDto.getAmount());
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<WalletDto> recharge(long walletId, TransactionDto transactionDto) throws PaymentServiceException {

        Optional<Wallet> wallet = _walletRepository.findById(walletId);

        //Check if wallet exist
        if (wallet.isPresent()) {
            Wallet walletDb = wallet.get();

            _paymentService.charge(transactionDto.getAmount());

            //Update the wallet balance
            walletDb = updateWalletBalance(transactionDto, walletDb, true);

            //Add the transaction to transactions history
            WalletTransactionHistory transactionHistory = addTransactionHistory(walletId, transactionDto, TransactionType.TYPE_CHARGE);

            //Return the walletDto
            WalletDto walletDto = new WalletDto(walletDb);
            walletDto.setLastTransaction(new WalletTransactionHistoryDto(transactionHistory));
            return Optional.of(walletDto);
        } else {
            return Optional.empty();
        }
    }

    private Wallet updateWalletBalance(TransactionDto transactionDto, Wallet walletDb, Boolean toBeAdded) {
        BigDecimal walletValue = walletDb.getBalance();
        if (toBeAdded) {
            walletValue = walletValue.add(transactionDto.getAmount());
        } else {
            walletValue = walletValue.subtract(transactionDto.getAmount());
        }
        walletDb.setBalance(walletValue);
        walletDb = _walletRepository.save(walletDb);
        return walletDb;
    }

    private WalletTransactionHistory addTransactionHistory(long walletId, TransactionDto transactionDto, String transactionType) {
        WalletTransactionHistory transactionHistory = new WalletTransactionHistory();
        transactionHistory.setType(transactionType);
        transactionHistory.setTimestamp(Instant.now());
        transactionHistory.setRequestTimestamp(Instant.ofEpochMilli(transactionDto.getRequestTimestamp()));
        transactionHistory.setQuantity(transactionDto.getAmount());
        transactionHistory.setWalletId(walletId);
        transactionHistory.setTransactionId(transactionDto.getTransactionId());
        _walletTransactionHistoryRepository.save(transactionHistory);
        return transactionHistory;
    }

    private WalletTransactionHistory getLastWalletTransactionHistory(long walletId) {
        return _walletTransactionHistoryRepository.findFirstByWalletIdOrderByTimestampDesc(walletId);
    }
}
