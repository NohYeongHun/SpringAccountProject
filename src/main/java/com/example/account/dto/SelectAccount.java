package com.example.account.dto;

import lombok.*;

import java.util.List;

public class SelectAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private List<AccountInfo> accountList;
    }
}
