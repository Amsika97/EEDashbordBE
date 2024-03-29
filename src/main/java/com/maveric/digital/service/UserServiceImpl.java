package com.maveric.digital.service;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.UserNotFoundException;
import com.maveric.digital.model.Roles;
import com.maveric.digital.model.User;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.LoginDto;
import com.maveric.digital.responsedto.UserDto;
import com.maveric.digital.responsedto.UserFilterDto;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.maveric.digital.utils.ServiceConstants.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ConversationService conversationService;

    @Override
    public synchronized UserDto createUser(UserDto userDto) {
        log.debug("UsersServiceImpl createUser() call started");
        if (userRepository.existsByEmailAddress(userDto.getEmailAddress())) {
            log.error("User already exists with emailAddress : {}", userDto.getEmailAddress());
            throw new CustomException(
                    String.format("User already exists with emailAddress : {%s } ",
                            userDto.getEmailAddress()),
                    HttpStatus.OK);
        }
        try {
            User user = conversationService.convertToUser(userDto);
            user.setCreatedDate(Instant.now());
            user.setLastLoginTime(Instant.now());
            User savedUser = userRepository.save(user);
            log.debug("User saved to db : {}", savedUser);
            log.debug("UsersServiceImpl createUser() call ended");
            return conversationService.convertToUserDto(savedUser);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(
                    String.format("Error  Occurs while saving User : exception-{%s} , userDto-{%s}", ex,
                            userDto));
        }
    }


    @Override
    public List<UserDto> createUserBulk(List<UserDto> usersDtoList) {
        log.debug("UsersServiceImpl createUserBulk() call started");
        for (UserDto userDto : usersDtoList) {
            if (userRepository.existsByEmailAddress(userDto.getEmailAddress())) {
                log.error("User already exists with emailAddress : {}", userDto.getEmailAddress());
                throw new CustomException(
                        String.format("User already exists with emailAddress : {%s } ",
                                userDto.getEmailAddress()),
                        HttpStatus.OK);
            }
        }
        List<User> createdUsers = conversationService.convertToUserList(usersDtoList);
        List<User> savedUsers = userRepository.saveAll(createdUsers);
        log.debug("User saved to db : {}", savedUsers);
        List<UserDto> savedUserDto = conversationService.convertToUserDtoList(savedUsers);
        log.debug("UsersServiceImpl createUserBulk(): call ended");
        return savedUserDto;
    }


    @Override
    public synchronized UserDto updateUserByEmail(String emailAddress, UserDto usersDto) {
        log.debug("UsersServiceImpl updateUserByEmail() call started");
        Optional<User> optionalUser = userRepository.findByEmailAddress(emailAddress);

        if (optionalUser.isEmpty()) {
            log.error("User not found for the emailAddress");
            throw new CustomException(
                    String.format("User not found for emailAddress : {%s } ", emailAddress), HttpStatus.OK);
        }
        User user = optionalUser.get();
        if (StringUtils.isNotBlank(usersDto.getUserName())) {
            user.setUserName(usersDto.getUserName());
        }
        if (StringUtils.isNotBlank(usersDto.getRole())) {
            user.setRole(usersDto.getRole());
        }
        if (StringUtils.isNotBlank(usersDto.getGroup())) {
            user.setGroup(usersDto.getGroup());
        }
        if (StringUtils.isNotBlank(usersDto.getBusinessUnitName())) {
            user.setBusinessUnitName(usersDto.getBusinessUnitName());
        }
        User savedUser = userRepository.save(user);
        log.debug("User saved to db {}", savedUser);
        log.debug("UsersServiceImpl updateUserByEmail() call ended");
        return conversationService.convertToUserDto(savedUser);
    }


    @Override
    public List<UserDto> getAllUser() {
        log.debug("UsersService- getAllUser() call started ");
        List<User> users = userRepository.findAll(Sort.by(Sort.Order.desc("createdDate")));
        if (CollectionUtils.isEmpty(users)) {
            log.error("UsersService::getAllUser() - No Users found");
            throw new CustomException("No Users found ", HttpStatus.OK);
        }
        List<UserDto> userDtos = users.stream().map(user -> conversationService.convertToUserDto(user))
                .toList();
        log.debug("UsersService::getAllUser() call completed");
        return userDtos;
    }


    @Override
    public UserDto getUserByEmailAddress(String emailAddress) {
        log.debug("UsersService- getUserByEmailAddress() call started ");
        Optional<User> user = userRepository.findByEmailAddress(emailAddress);
        if (user.isEmpty()) {
            log.error("User not found with email address from db::{}", emailAddress);
            throw new CustomException(
                    String.format("User not found for emailAddress : {%s } ", emailAddress),
                    HttpStatus.OK);
        }
        UserDto userDto = conversationService.convertToUserDto(user.get());
        log.debug("UsersService- getUserByEmailAddress() call ended ");
        return userDto;
    }


    @Override
    public void deactivateUserByEmail(String emailAddress) {
        log.debug("UsersService - deactivateUserByEmail() call started ");

        Optional<User> optionalUser = userRepository.findByEmailAddress(emailAddress);
        if (optionalUser.isEmpty()) {
            throw new CustomException("User not found", HttpStatus.OK);
        }
        User user = optionalUser.get();
        if (Boolean.FALSE.equals(user.getIsActive())) {
            log.error("User is already inactive.");
            throw new CustomException(
                    String.format("User is already inactive for emailAddress : {%s } ", emailAddress),
                    HttpStatus.OK);
        }
        user.setIsActive(false);
        userRepository.save(user);
        log.debug("User's isActive status set to false");
        log.debug("UsersService - deactivateUserByEmail() call started ");
    }

    @Override
    public LoginDto loginUser(String userName, String password) throws CloneNotSupportedException {
        log.debug("UsersService - loginUser() call started ");
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            log.debug("User not found for userName: {}", userName);
            throw new UserNotFoundException("User not found");
        }
        LoginDto loginDto = new LoginDto();
        if (user.get().getPassword().equalsIgnoreCase(password)) {
            loginDto = conversationService.toLoginDto(user.get().clone());
            user.get().setLastLoginTime(Instant.now());
            userRepository.save(user.get());
            log.debug("Login successful for userName: {}", userName);
            return loginDto;
        } else {
            loginDto.setLoginMessage(Login_Failed);
            log.debug("Login failed for userName: {}", userName);
            log.debug("UsersService - loginUser() call end ");
            return loginDto;
        }
    }

    @Override
    public User createOrUpdateUser(UserDto userDto) throws CloneNotSupportedException {
        log.debug("UsersService - createOrUpdateUser() call started ");
        User userResponse = null;
        Optional<User> userObj = userRepository.findByOid(userDto.getOid());
        if (userObj.isPresent()) {
            log.debug("User Details::{}", userObj.get());
            userResponse = userObj.get().clone();
            userObj.get().setLastLoginTime(Instant.now());
            userRepository.save(userObj.get());
            log.debug("User Details after Update::{}", userObj.get());
            log.debug("UsersService - createOrUpdateUser() call end ");
            return userResponse;
        }
        Instant instant = Instant.now();
        User user = conversationService.convertToUser(userDto);
        user.setCreatedDate(instant);
        user.setLastLoginTime(instant);
        userRepository.insert(user);
        log.debug("User Details on save::{}", user);
        log.debug("UsersService - createOrUpdateUser() call end ");
        return user;
    }

    @Override
    public Map<String, List<UserFilterDto>> getUsersForDropDown() {
        log.debug("UsersService - getUsersForDropDown() call started ");
        Map<String, List<UserFilterDto>> reponseMap = new HashMap<>();
        Optional<List<User>> filteredUsers = userRepository.findByRoleIn(List.of(Roles.User, Roles.Reviewer));
        log.debug("users with role user/reviewer is fetched from DB {}", filteredUsers);
        if (filteredUsers.isPresent()) {
            reponseMap.put(USER, getReponse(filteredUsers.get(), Roles.User));
            log.debug("reponse for users having user role");
            reponseMap.put(REVIEWER, getReponse(filteredUsers.get(), Roles.Reviewer));
            log.debug("reponse for users having reviewer role");
        }
        log.debug("UsersService - getUsersForDropDown() call end ");
        return reponseMap;
    }

    public List<UserFilterDto> getReponse(List<User> filteredUsers, Roles role) {
        return conversationService.toUserFilterDtos(filteredUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(String.valueOf(role)))
                .collect(Collectors.toList()));

    }

    @Override
    public List<UserFilterDto> getListOfUsersForDropDown() {
        log.info("UsersService - getUsersForDropDown() call started ");
        List<User> userList = userRepository.findAll();
        log.info("UsersService - getUsersForDropDown() call end ");
        return conversationService.toUserFilterDtos(userList);

    }
}