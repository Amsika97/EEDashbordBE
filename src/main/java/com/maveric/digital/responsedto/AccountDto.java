package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDto {
    public AccountDto(String accountName, Long accountId) {
        this.accountName = accountName;
        this.accountId = accountId;
    }
    private String accountName;
    private Long accountId;

}
