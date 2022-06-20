package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.*;
import com.example.account.service.AccountService;
import com.example.account.service.AccountUserService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final AccountUserService accountUserService;
    private final RedisTestService redisTestService;


    @GetMapping("/get-lock")
    public String getLock(){
        return redisTestService.getLock();
    }

    @PostMapping("/create-account")
    public CreateAccount.Response registerAccount(
            // convert DTO to entity
                @RequestBody @Valid CreateAccount.Request request
            ){

        Account account = accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance()
        );

        // convert Entity to DTO
        return new CreateAccount.Response(
                account.getAccountUser().getId(),
                account.getAccountNumber(),
                LocalDateTime.now()
        );

    }

    @PostMapping("/create-user")
    public CreateUser.Response registerUser(
            // convert DTO to entity
            @RequestBody @Valid CreateUser.Request request
    ){

        AccountUser user = accountUserService.createAccountUser(
                request.getName(),
                request.getPrivateNumber()
        );

        // convert Entity to DTO
        return new CreateUser.Response(
                user.getId(),
                user.getName(),
                user.getPrivateNumber(),
                LocalDateTime.now()
        );

    }

    @PutMapping("/unregister-account")
    public DeleteAccount.Response unRegisterAccount(
            // convert DTO to entity
            @RequestBody @Valid DeleteAccount.Request request
    ){

        Account account = accountService.unRegisteredAccount(
                request.getUserId(),
                request.getAccountNumber()
        );

        // convert Entity to DTO
        return new DeleteAccount.Response(
                account.getAccountUser().getId(),
                account.getAccountNumber(),
                account.getUnRegisteredAt()
        );

    }

    @GetMapping("/get/{userId}")
    public SelectAccount.Response getAccount(
            // convert DTO to entity
            @PathVariable("userId") @Valid Long userId
    ){

        List<AccountInfo> accountList = accountService.getAccountList(
                userId
        );

        // convert Entity to DTO
        return new SelectAccount.Response(
                accountList
        );

    }
}
