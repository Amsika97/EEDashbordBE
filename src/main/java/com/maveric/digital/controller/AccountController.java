package com.maveric.digital.controller;

import com.maveric.digital.model.Account;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.service.AccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);


    @Operation(summary = "Save Account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDto.class))
    )})
    @PostMapping(path = "/account/save")
    public ResponseEntity<Account> saveAccount(@RequestBody @Valid AccountDto requestPayload) {
        logger.debug("AccountController::saveAccount::{}",
                requestPayload);
        return ResponseEntity.ok(accountServiceImpl.createAccount(requestPayload));
    }

    @Operation(summary = "Get All Accounts")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDto.class))
    )})
    @GetMapping(path = "/account/all")
    public ResponseEntity<List<AccountDto>> getAccountsAll() {
        logger.debug("AccountController::getAccountsAll() started");
        List<AccountDto> accountList= accountServiceImpl.getAllAccounts();
        logger.debug("AccountController::getAccountsAll() ended");
        return ResponseEntity.ok(accountList);
    }

}


