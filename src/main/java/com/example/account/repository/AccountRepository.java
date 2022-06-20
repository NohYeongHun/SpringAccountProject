package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountInfo;
import com.example.account.type.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Account Table 접근 위한 인터페이스
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    Optional<Account> findByAccountUserAndAccountNumber(
            AccountUser accountUser, String accountNumber
    );
    Optional<Account> findByAccountNumber(String accountNumber);
    List<AccountInfo> findByAccountUserAndAccountStatus(AccountUser accountUser, AccountStatus accountStatus);
}
