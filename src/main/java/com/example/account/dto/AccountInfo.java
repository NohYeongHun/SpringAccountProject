package com.example.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountInfo {
    private String accountNumber;
    private Long balance;
}
