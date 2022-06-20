package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountInfo;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.account.type.AccountStatus.IN_USE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountUser accountUser;

    @Mock
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
    @DisplayName("계좌리스트 조회 성공")
    void testGetAccountList(){
        //given
        List<Account> accountList = new ArrayList<>();
        accountList.add(account);
        given(accountRepository.findAll())
                .willReturn(accountList);

        //when
        List<Account> findAccountList = accountRepository.findAll();

        //then
        assertEquals(1, findAccountList.size());
        assertEquals(account.getAccountNumber(), findAccountList.get(0).getAccountNumber());
        assertEquals(account.getBalance(), findAccountList.get(0).getBalance());
    }
    @Test
    @DisplayName("계좌 리스트 조회 실패 - 사용자 없음")
    void testFailedGetAccountListNotFoundUser(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountList(1L)
        );

        //then
        verify(accountUserRepository).findById(anyLong());
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }


    @Test
    @DisplayName("계좌 리스트 조회 실패 - 계좌 없음")
    void testFailedGetAccountListNotFoundAccount(){
        //given
        AccountUser user = AccountUser.builder()
                .id(1L)
                .name("PoPo")
                .privateNumber("10001000").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountList(1L)
        );

        //then
        verify(accountUserRepository).findById(anyLong());
        assertEquals(exception.getErrorCode(), ErrorCode.ACCOUNT_NOT_FOUND);
    }
    
    @Test
    @DisplayName("계좌 생성 성공")
    void testCreateAccount(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(AccountUser.builder()
                        .id(1L)
                        .name("nyh")
                        .privateNumber("1000001")
                        .build()));
        given(accountRepository.save(any()))
                .willReturn(
                        Account.builder()
                                .accountUser(accountUser)
                                .accountStatus(IN_USE)
                                .accountNumber(account.getAccountNumber())
                                .balance(account.getBalance())
                                .registeredAt(LocalDateTime.now())
                                .build()
                );

        //when
        Account compareAccount = accountService.createAccount(
                accountUser.getId(), account.getBalance()
        );

        //then
        assertEquals(compareAccount.getAccountNumber(), account.getAccountNumber());
        assertEquals(compareAccount.getBalance(), account.getBalance());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 사용자 없음")
    void testCreateAccountNotUserFailed(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 10000L)
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 생성 실패 - 계좌 10개 이상")
    void testCreateAccountMaxFailed(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        List<AccountInfo> accountList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            accountList.add(null);
        }

        given(accountRepository.findByAccountUserAndAccountStatus(any(), any()))
                .willReturn(accountList);

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(accountUser.getId(), 1000L)
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.MAX_ACCOUNT_PER_USER_10);
    }

    @Test
    @DisplayName("계좌 해지 성공")
    void testUnRegisterAccount(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(AccountUser.builder()
                        .id(accountUser.getId())
                        .name(accountUser.getName())
                        .privateNumber(accountUser.getPrivateNumber())
                        .build()));

        given(accountRepository
                .findByAccountUserAndAccountNumber(any(), anyString()))
                .willReturn(Optional.of(Account.builder()
                                .id(account.getId())
                                .accountUser(accountUser)
                                .accountStatus(AccountStatus.IN_USE)
                                .accountNumber(account.getAccountNumber())
                                        .build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .id(account.getId())
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber(account.getAccountNumber())
                        .build());
        //when
        Account compareAccount = accountService.unRegisteredAccount(
                accountUser.getId(), account.getAccountNumber()
        );

        //then
        assertEquals(AccountStatus.UNREGISTERED, compareAccount.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void testUnRegisterFail_userUnMatch(){
        //given
        AccountUser compareUser = AccountUser.builder()
                .id(13L)
                .name("Harry")
                .privateNumber("10000222")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));


        given(accountRepository.findByAccountUserAndAccountNumber(any(), any()))
                .willReturn(Optional.of(Account.builder()
                                .accountUser(compareUser)
                                .balance(0L)
                                .accountNumber(account.getAccountNumber())
                        .build()));
        //when
        AccountException exception = assertThrows(AccountException.class,
                    () -> accountService.unRegisteredAccount(accountUser.getId(), account.getAccountNumber())
                );
        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }


    @Test
    @DisplayName("계좌 해지 실패 - 사용자 없음")
    void testUnRegisterFail_userNotFound(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.unRegisteredAccount(accountUser.getId(), account.getAccountNumber())
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌를 찾을 수 없음")
    void testUnRegisterFail_accountNotFound(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountUserAndAccountNumber(any(), anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.unRegisteredAccount(accountUser.getId(), account.getAccountNumber())
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 이미 해지한 경우")
    void testUnRegisterFail_alreadyUnRegister(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountUserAndAccountNumber(any(), anyString()))
                .willReturn(Optional.of(Account.builder()
                                .accountNumber(account.getAccountNumber())
                                .accountUser(accountUser)
                                .balance(account.getBalance())
                                .accountStatus(AccountStatus.UNREGISTERED)
                        .build()));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.unRegisteredAccount(accountUser.getId(), account.getAccountNumber())
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.ALREADY_UNREGISTERED);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 잔액이 남아있음")
    void testUnRegisterFail_existBalance(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountUserAndAccountNumber(any(), anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.unRegisteredAccount(accountUser.getId(), account.getAccountNumber())
        );

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.EXISTS_BALANCE);
    }
    

}
