package com.maveric.digital.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.maveric.digital.exceptions.AccountsNotFoundException;
import com.maveric.digital.exceptions.BusinessUnitCreationException;
import com.maveric.digital.model.Account;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.utils.AccountMapper;
import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AccountServiceTest {

	private final String ACCOUNT_NOT_FOUND = "Account Not Found";
	@MockBean
	private AccountRepository accountRepository;




	@Autowired
	private AccountServiceImpl accountService;

  @BeforeEach
   void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
  }


    @Test
    void testCreateAccount() {
        AccountDto accountDto = new AccountDto();
        when(accountRepository.save(any(Account.class))).thenReturn(new Account());
        Account result = accountService.createAccount(accountDto);
        assertNotNull(result);
        verify(accountRepository).save(any(Account.class));
    }





}
