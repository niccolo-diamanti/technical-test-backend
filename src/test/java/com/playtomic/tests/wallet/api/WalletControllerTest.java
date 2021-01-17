package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Test
    void createWallet_shouldReturnCreatedWallet() throws Exception {

        final UUID userId = UUID.randomUUID();

        var walletToCreate = WalletDto.builder()
                .balance(BigDecimal.valueOf(100))
                .userId(userId).build();

        var expectedWallet = WalletDto.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(100))
                .userId(userId).build();

        when(walletService.createWallet(walletToCreate)).thenReturn(Optional.of(expectedWallet));

        mvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(walletToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(100)))
                .andExpect(jsonPath("$.userId", is(userId.toString())));
    }

    @Test
    void createWallet_shouldReturn400IfWalletIsNotValid() throws Exception {

        final UUID userId = UUID.randomUUID();

        var walletToCreate = WalletDto.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(100))
                .userId(userId).build();

        mvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(walletToCreate)))
                .andExpect(status().isBadRequest());
    }

    //TODO other tests
}