package com.maveric.digital.service;

import com.maveric.digital.model.Account;
import com.maveric.digital.responsedto.AccountDto;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountDto requestPayload);
}
