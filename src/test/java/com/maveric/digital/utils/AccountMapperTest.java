package com.maveric.digital.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.maveric.digital.model.Account;
import com.maveric.digital.responsedto.AccountDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class AccountMapperTest {
  @InjectMocks
  private AccountMapper accountMapper;
  @Test
  void testToAccountDtoSingle() {
    Long expectedId = 123L;
    String expectedName = "Test Account";
    Account account = new Account();
    account.setId(expectedId);
    account.setAccountName(expectedName);

    AccountDto result = accountMapper.toAccountDto(account);

    assertNotNull(result, "AccountDto should not be null");
    assertEquals(expectedId, result.getAccountId(), "Account ID should match");
    assertEquals(expectedName, result.getAccountName(), "Account name should match");
  }
  @Test
  void testToAccountDtoList() {
    Account account1 = new Account();
    account1.setId(123L);
    account1.setAccountName("Test Account 1");

    Account account2 = new Account();
    account2.setId(456L);
    account2.setAccountName("Test Account 2");

    List<Account> accountList = Arrays.asList(account1, account2);

    List<AccountDto> resultList = accountMapper.toAccountDto(accountList);

    assertNotNull(resultList, "Resultant list should not be null");
    assertEquals(2, resultList.size(), "Resultant list size should match input list size");
    assertEquals(account1.getId(), resultList.get(0).getAccountId(), "First account ID should match");
    assertEquals(account1.getAccountName(), resultList.get(0).getAccountName(), "First account name should match");
    assertEquals(account2.getId(), resultList.get(1).getAccountId(), "Second account ID should match");
    assertEquals(account2.getAccountName(), resultList.get(1).getAccountName(), "Second account name should match");
  }
}
