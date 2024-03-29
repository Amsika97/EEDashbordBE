package com.maveric.digital.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.model.Account;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.service.AccountServiceImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(AccountController.class)
@ExtendWith(SpringExtension.class)
class AccountControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AccountController accountController;
  @MockBean
  private AccountServiceImpl accountService;



  @Test
  void testSaveAccount() throws Exception {


    Account account = new Account();
    account.setAccountName("Divya");
    account.setCreatedAt(1L);
    account.setId(1L);
    account.setUpdatedAt(1L);
    when(accountService.createAccount(Mockito.any())).thenReturn(account);

    AccountDto accountDto = new AccountDto();
    accountDto.setAccountId(1L);
    accountDto.setAccountName("Divya");
    String content = new ObjectMapper().writeValueAsString(accountDto);

    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/account/save")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(content);
    MockMvcBuilders.standaloneSetup(accountController)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"accountName\":\"Divya\",\"createdAt\":1,\"updatedAt\":1}"));
  }
}
