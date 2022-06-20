package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.AccountService;
import com.example.account.service.AccountUserService;
import com.example.account.service.RedisTestService;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.account.type.AccountStatus.UNREGISTERED;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisTestService redisTestService;

    @MockBean
    private AccountUserService accountUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private AccountUser accountUser;

    private Account account;

    @BeforeEach
    void setup(){
        accountUser = AccountUser.builder()
                .id(1L)
                .name("nyh")
                .privateNumber("1000001")
                .build();

        account = Account.builder()
                .id(1L)
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000)
                .accountNumber("1000000001")
                .build();
    }


    @Test
    @DisplayName("계좌 리스트 조회 성공")
    void testSuccessGetAccountList() throws Exception {
        //given
        List<AccountInfo> accountList = new ArrayList<>();
        AccountInfo dto = new AccountInfo(
                account.getAccountNumber(), account.getBalance()
        );
        accountList.add(dto);

        given(accountService.getAccountList(anyLong()))
                .willReturn(accountList);
        //when
        //then
        mockMvc.perform(get(String.format("/account/get/%d", accountUser.getId())))
                .andDo(print())
                .andExpect(jsonPath("$.accountList[0].accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.accountList[0].balance").value(account.getBalance()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계좌 리스트 조회 실패")
    void failGetAccountList() throws Exception {
        //given
        given(accountService.getAccountList(anyLong()))
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/account/get/2"))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("계좌를 찾을 수 없습니다."))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계좌 리스트 생성 성공")
    void testSuccessCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(account);
        //when
        //then
        mockMvc.perform(post("/account/create-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateAccount.Request(accountUser.getId(), account.getBalance())
                ))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.accountNumber").value("1000000001"));

    }

    @Test
    @DisplayName("계좌 해지 성공")
    void testSuccessUnRegisterAccountList() throws Exception {
        //given
        given(accountService.unRegisteredAccount(anyLong(), anyString()))
                .willReturn(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(UNREGISTERED)
                        .balance(account.getBalance())
                        .accountNumber(account.getAccountNumber())
                        .registeredAt(account.getRegisteredAt())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(put("/account/unregister-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(account.getAccountUser().getId(), account.getAccountNumber())
                        ))
                ).andDo(print())
                .andExpect(jsonPath("$.userId").value(String.format("%s", account.getAccountUser().getId())))
                .andExpect(jsonPath("$.accountNumber").value(String.format("%s", account.getAccountNumber())))
                .andExpect(status().isOk());
    }

}