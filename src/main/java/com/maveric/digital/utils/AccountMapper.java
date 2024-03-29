package com.maveric.digital.utils;

import com.maveric.digital.model.Account;
import com.maveric.digital.responsedto.AccountDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountDto toAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(account.getId());
        accountDto.setAccountName(account.getAccountName());

        return accountDto;
    }

    public List<AccountDto> toAccountDto(List<Account> accountList) {
        return accountList.stream()
                .map(this::toAccountDto)
                .collect(Collectors.toList());
    }
}