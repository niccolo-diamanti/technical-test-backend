package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.data.model.Wallet;
import com.playtomic.tests.wallet.data.model.WalletTransactionHistory;
import com.playtomic.tests.wallet.data.repository.WalletRepository;
import com.playtomic.tests.wallet.data.repository.WalletTransactionHistoryRepository;
import com.playtomic.tests.wallet.dto.TransactionType;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.dto.WalletTransactionHistoryDto;
import com.playtomic.tests.wallet.service.impl.WalletServiceImpl;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletServiceImpl _walletService;

    @Mock
    private WalletRepository _walletRepository;

    @Mock
    private WalletTransactionHistoryRepository _walletTransactionHistoryRepository;

    @Test
    void createWallet_shouldCreateWallet() {
        //Given
        var wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(100))
                .userId(UUID.randomUUID()).build();
        given(_walletRepository.save(wallet)).willReturn(wallet);

        //When
        Optional<WalletDto> optionalWalletDto = _walletService.createWallet(new WalletDto(wallet));

        //Then
        assertTrue(optionalWalletDto.isPresent());
        assertThat(optionalWalletDto.get().getBalance(), is(BigDecimal.valueOf(100)));
    }

    @Test
    void findWalletById_shouldFindWallet() {
        //Given
        final long id = 1;
        var walletTransactionHistory = WalletTransactionHistory.builder()
                .transactionId(UUID.randomUUID())
                .id(id)
                .quantity(BigDecimal.valueOf(10))
                .requestTimestamp(Instant.now())
                .timestamp(Instant.now())
                .type(TransactionType.TYPE_RECHARGE).build();

        var walletDto = WalletDto.builder()
                .balance(BigDecimal.valueOf(100))
                .userId(UUID.randomUUID())
                .id(id)
                .lastTransaction(new WalletTransactionHistoryDto(walletTransactionHistory)).build();

        Wallet wallet = new Wallet(walletDto);
        given(_walletRepository.findById(id)).willReturn(Optional.of(wallet));
        given(_walletTransactionHistoryRepository.findFirstByWalletIdOrderByTimestampDesc(id)).willReturn(walletTransactionHistory);

        //When
        Optional<WalletDto> optionalWalletDto = _walletService.findWalletById(id);

        //Then
        assertTrue(optionalWalletDto.isPresent());
        assertThat(optionalWalletDto.get(), is(walletDto));
    }

    @Test
    void findWalletByUserId_shouldFindWallet() {
        //Given
        final long id = 1;
        final UUID userId = UUID.randomUUID();
        var walletTransactionHistory = WalletTransactionHistory.builder()
                .transactionId(userId)
                .id(id)
                .quantity(BigDecimal.valueOf(10))
                .requestTimestamp(Instant.now())
                .timestamp(Instant.now())
                .type(TransactionType.TYPE_RECHARGE).build();

        var walletDto = WalletDto.builder()
                .balance(BigDecimal.valueOf(100))
                .userId(userId)
                .id(id)
                .lastTransaction(new WalletTransactionHistoryDto(walletTransactionHistory)).build();

        Wallet wallet = new Wallet(walletDto);
        given(_walletRepository.findByUserId(userId)).willReturn(Optional.of(wallet));
        given(_walletTransactionHistoryRepository.findFirstByWalletIdOrderByTimestampDesc(id)).willReturn(walletTransactionHistory);

        //When
        Optional<WalletDto> optionalWalletDto = _walletService.findWalletByUserId(userId);

        //Then
        assertTrue(optionalWalletDto.isPresent());
        assertThat(optionalWalletDto.get(), is(walletDto));
    }

    //TODO other tests
}