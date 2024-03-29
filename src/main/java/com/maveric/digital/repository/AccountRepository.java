package com.maveric.digital.repository;

import com.maveric.digital.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account,Long> {
    List<Account> findAll();
    List<Account> findAccountsByIdIn(List<Long> accountIds);


    List<Account> findAccountsDistinctByAccountName();

}
