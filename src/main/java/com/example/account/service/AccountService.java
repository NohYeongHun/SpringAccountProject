package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountInfo;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.account.type.AccountStatus.IN_USE;

@Service
@RequiredArgsConstructor
public class AccountService{
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성하고
     * 계좌를 저장하고, 그 정보를 넘긴다.
     * 해당 사용자가 10개 이상의 계좌를 가지고 있다면 생성이 불가하다.
     * 사용자가 10개 계좌를 가지고 있는 경우는 REGISTER 상태가 IN_USE 인 경우이다.
     */
    @Transactional
    public Account createAccount(Long userId, Long initialBalance){
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        List<AccountInfo> accountList = accountRepository.findByAccountUserAndAccountStatus(accountUser, IN_USE);

        validateCreateAccount(accountList);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        return accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );
    }

    /**
     *
     * 사용자가 없는 경우 USER NOT FOUND
     * 사용자 아이디와 계좌 소유주가 다른 경우 ACCOUNT NOT FOUND
     * 계좌가 이미 해지 상태인 경우 ALREADY UNREGISTERED
     * 잔액이 있는 경우 EXISTS BALANCE
     */
    @Transactional
    public Account unRegisteredAccount(Long userId, String accountNumber){
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountUserAndAccountNumber(accountUser, accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUnRegister(account, userId);

        return accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber(account.getAccountNumber())
                        .balance(account.getBalance())
                        .registeredAt(account.getRegisteredAt())
                        .unRegisteredAt(LocalDateTime.now())
                        .build()
        );
    }

    private void validateCreateAccount(List<AccountInfo> accountList){
        if(accountList.size() == 10)
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
    }

    private void validateUnRegister(Account account, Long userId){

        if(AccountStatus.UNREGISTERED.equals(account.getAccountStatus()))
            throw new AccountException(ErrorCode.ALREADY_UNREGISTERED);

        if(account.getBalance() != 0)
            throw new AccountException(ErrorCode.EXISTS_BALANCE);

        if(!account.getAccountUser().getId().equals(userId))
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
    }


    /**
     *
     * 사용자가 없는 경우 USER NOT FOUND
     * 사용자 아이디와 사용자 상태가 IN_USE 인 계좌리스트 반환
     */
    @Transactional
    public List<AccountInfo> getAccountList(Long userId){
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        List<AccountInfo> accountList = accountRepository.findByAccountUserAndAccountStatus(accountUser, IN_USE);
        if (accountList.size() == 0)
            throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);

        return accountList;
    }

}
