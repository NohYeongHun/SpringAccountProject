package com.example.account.service;

import com.example.account.domain.AccountUser;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountUserService{
    private final AccountUserRepository accountUserRepository;

    /**
     * 사용자 이름과 사용자 ID를 받는다.
     * 받은 사용자 이름과 동일한 사용자가 있는지 확인한다.
     * privateNumber 유효성을 검증한다.
     */
    @Transactional
    public AccountUser createAccountUser(String name, String privateNumber){

        Optional<AccountUser> accountUser = accountUserRepository.findByPrivateNumber(privateNumber);
        if(accountUser.isPresent()){
            throw new AccountException(ErrorCode.EXIST_SAME_PRIVATE_NUMBER);
        }

        return accountUserRepository.save(
                AccountUser.builder()
                        .name(name)
                        .privateNumber(privateNumber)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );

    }

}
