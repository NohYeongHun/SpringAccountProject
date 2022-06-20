package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateUser {
    @Getter
    @Setter
    public static class Request{
        @NotNull
        private String privateNumber;

        @NotNull
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private Long id;
        private String name;
        private String privateNumber;
        private LocalDateTime registeredAt;
    }
}
