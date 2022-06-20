package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {

    // PK, Auto Increment
    @Id
    @GeneratedValue
    private Long id;

    // join
    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    // Enum 값의 문자열을 그대로 DB에 저장.
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void useBalance(Long amount){
        if (amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }

        balance -= amount;
    }

    public void cancelBalance(Long amount){
        if (amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }

        balance += amount;
    }

}
