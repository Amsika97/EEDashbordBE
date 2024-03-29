package com.maveric.digital.service;

import com.maveric.digital.model.User;
import com.maveric.digital.responsedto.LoginDto;
import com.maveric.digital.responsedto.UserDto;
import com.maveric.digital.responsedto.UserFilterDto;

import java.util.List;
import java.util.Map;

public interface UserService {
  UserDto createUser(UserDto user);

  List<UserDto> createUserBulk(List<UserDto> usersDtos);

  UserDto updateUserByEmail(String emailAddress, UserDto usersDto);

  List<UserDto> getAllUser();

  UserDto getUserByEmailAddress(String emailAddress);

  void deactivateUserByEmail(String emailAddress);
  LoginDto loginUser(String userName, String password) throws CloneNotSupportedException;
  
  User createOrUpdateUser(UserDto userDto) throws CloneNotSupportedException;
  Map<String,List<UserFilterDto>> getUsersForDropDown();

  List<UserFilterDto> getListOfUsersForDropDown();
}
