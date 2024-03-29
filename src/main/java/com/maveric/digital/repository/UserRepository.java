package com.maveric.digital.repository;
import com.maveric.digital.model.Roles;
import com.maveric.digital.model.User;
import java.util.List;

import com.maveric.digital.responsedto.LoginDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
  boolean existsByEmailAddress(String emailAddress);
  Optional<User> findByEmailAddress(String emailAddress);
  List<User> findAll();
  Optional<User> findByUserName(String username);
  Optional<User> findByOid(UUID oid);
  Optional<List<User>> findByOidIn(List<UUID> oid);
  Optional<List<User>> findByRoleIn(List<Roles> role);

}
