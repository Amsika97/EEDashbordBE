package com.maveric.digital.service;

import com.maveric.digital.model.Account;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.utils.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ConversationService conversationService;

    private final AccountMapper accountMapper;
    private final ProjectRepository projectRepository;

    public static final String ACCOUNT_NOT_FOUND = "Account Not Found";

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);


    @Override
    public Account createAccount(AccountDto accountDto) {
        logger.debug("AccountServiceImpl::createAccount()::Start");
        Account newAccount = new Account();
        newAccount.setAccountName(accountDto.getAccountName());
        newAccount.setCreatedAt(System.currentTimeMillis());
        return accountRepository.save(newAccount);
    }


    public List<AccountDto> getAllAccounts() {
        logger.debug("AccountServiceImpl::getAllAccounts()::Start");
        List<Account> accounts = accountRepository.findAll();
        if (CollectionUtils.isEmpty(accounts)) {
            return new ArrayList<>();
        }
        Map<String, Account> map = new HashMap<>();
        accounts.stream().filter(account -> !map.containsKey(account.getAccountName())).forEachOrdered(account -> map.put(account.getAccountName(), account));
        List<AccountDto> accountDtoList = accountMapper.toAccountDto(map.values().stream().toList());
        logger.debug("AccountServiceImpl::getAllAccounts()::End");
        return accountDtoList;


    }
}