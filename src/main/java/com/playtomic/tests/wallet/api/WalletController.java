package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.dto.TransactionDto;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.service.WalletService;
import com.playtomic.tests.wallet.service.error.InsufficientBalanceException;
import com.playtomic.tests.wallet.service.error.PaymentServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController(value = "/wallet")
@Slf4j
@RequiredArgsConstructor
public class WalletController {

    private final WalletService _walletService;

    @PostMapping(value = "/create")
    @Operation(description = "Create a Wallet")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success - Wallet created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Object provided cannot be created",
                    content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    ResponseEntity<WalletDto> createWallet(
            @Parameter(name = "walletDto", description = "Wallet Object to create") @RequestBody WalletDto walletDto
    ) {
        if (walletDto.getId() != null) {
            //WalletDto must not already have an id
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Optional<WalletDto> wallet = _walletService.createWallet(walletDto);
            return wallet.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
    }

    @GetMapping(value = "/find")
    @Operation(description = "Find a Wallet by identifier or by user identifier")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success - Wallet created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Both walletId and userId parameters are null",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Wallet not found for input parameters",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
    })
    ResponseEntity<WalletDto> findWallet(
            @RequestParam(value = "walletId", required = false) @Parameter(name = "walletId", description = "Unique identifier of the wallet") Long walletId,
            @RequestParam(value = "userId", required = false) @Parameter(name = "userId", description = "Unique identifier of the user") UUID userId
    ) {
        Optional<WalletDto> walletById;

        if (walletId != null) {
            walletById = _walletService.findWalletById(walletId);
        } else if (userId != null) {
            walletById = _walletService.findWalletByUserId(userId);
        } else {
            //If both parameters are null return 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return walletById.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    //TODO pay and recharge methods can be merged if we add a switch based on the TransactionType
    @PostMapping(value = "/pay/{walletId}")
    @Operation(description = "Charge a wallet to pay services")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success - Wallet created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Wallet cannot be charged to pay",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Wallet not found for input parameters",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
    })
    ResponseEntity<WalletDto> pay(
            @PathVariable(value = "walletId", required = false) @Parameter(name = "walletId", description = "Unique identifier of the wallet") Long walletId,
            @Parameter(name = "transactionDto", description = "Transaction Object") @Valid @RequestBody TransactionDto transactionDto
    ) {
        log.info("Charging wallet {} [transaction id: {}]", walletId, transactionDto.getTransactionId());
        Optional<WalletDto> walletById;

        if (walletId != null) {
            try {
                walletById = _walletService.pay(walletId, transactionDto);
            } catch (InsufficientBalanceException exception) {
                log.error("Wallet {} cannot be charged for the selected amount [transaction id: {}; exception: {}]", walletId, transactionDto.getTransactionId(), exception.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            //If both parameters are null return 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return walletById.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //TODO pay and recharge methods can be merged if we add a switch based on the TransactionType
    @PostMapping(value = "/recharge/{walletId}")
    @Operation(description = "Recharge a wallet")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success - Wallet created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Wallet cannot be recharged",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Wallet not found for input parameters",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}),
    })
    ResponseEntity<WalletDto> recharge(
            @PathVariable(value = "walletId", required = false) @Parameter(name = "walletId", description = "Unique identifier of the wallet") Long walletId,
            @Parameter(name = "transactionDto", description = "Transaction Object") @Valid @RequestBody TransactionDto transactionDto
    ) {
        log.info("Recharging wallet {} [transaction id: {}]", walletId, transactionDto.getTransactionId());
        Optional<WalletDto> walletById;

        if (walletId != null) {
            try {
                walletById = _walletService.recharge(walletId, transactionDto);
            } catch (PaymentServiceException exception) {
                log.error("Wallet {} cannot be recharged [transaction id: {}; exception: {}]", walletId, transactionDto.getTransactionId(), exception.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            //If both parameters are null return 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return walletById.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
