package com.maveric.digital.service;

import static com.mongodb.assertions.Assertions.assertFalse;
import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.UserNotFoundException;
import com.maveric.digital.model.User;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.LoginDto;
import com.maveric.digital.responsedto.UserDto;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversationService conversationService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;
    private List<UserDto> userDtoList;
    private List<User> userList;

    @BeforeEach
    void initiateUserDtoData() {
        userDto = new UserDto();
        userDto.setEmailAddress("user@example.com");
        userDto.setUserName("John");
        userDto.setEmailAddress("john@example.com");
        userDto.setGroup("business");
        userDto.setBusinessUnitName("internal");
        userDto.setRole("accosiate");
        userDto.setIsActive(true);

        user = new User();
        user.setCreatedDate(Instant.now());
        user.setLastLoginTime(Instant.now());
        user.setIsActive(true);
        user.setPassword("Sri@12345");
    }

    @BeforeEach
    void setUp() {
        UserDto userDto1 = new UserDto();
        userDto1.setEmailAddress("user1@example.com");
        UserDto userDto2 = new UserDto();
        userDto2.setEmailAddress("user2@example.com");
        userDtoList = Arrays.asList(userDto1, userDto2);

        User user1 = new User();
        user1.setEmailAddress(userDto1.getEmailAddress());
        User user2 = new User();
        user2.setEmailAddress(userDto2.getEmailAddress());
        userList = Arrays.asList(user1, user2);
    }

    @Test
    void createUserSuccessfully() {

        when(userRepository.existsByEmailAddress(userDto.getEmailAddress())).thenReturn(false);
        when(conversationService.convertToUser(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(conversationService.convertToUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getEmailAddress(), result.getEmailAddress());
        assertEquals(userDto.getUserName(), result.getUserName());

    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.existsByEmailAddress(userDto.getEmailAddress())).thenReturn(true);

        CustomException thrown = assertThrows(
                CustomException.class,
                () -> userService.createUser(userDto),
                "User already exists"
        );

        assertTrue(thrown.getMessage().contains(userDto.getEmailAddress()));
        assertEquals(HttpStatus.OK, thrown.getHttpStatus());
    }

    @Test
    void shouldThrowDataIntegrityViolationExceptionOnSave() {

        when(userRepository.existsByEmailAddress(userDto.getEmailAddress())).thenReturn(false);
        when(conversationService.convertToUser(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Constraint violation"));

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void shouldThrowResourceCreationExceptionOnSave() {

        when(userRepository.existsByEmailAddress(userDto.getEmailAddress())).thenReturn(false);
        when(conversationService.convertToUser(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenThrow(new ResourceCreationException("Constraint violation"));

        assertThrows(ResourceCreationException.class, () -> userService.createUser(userDto));
    }


    @Test
    void shouldCreateUsersInBulkSuccessfully() {
        for (UserDto userDto : userDtoList) {
            when(userRepository.existsByEmailAddress(userDto.getEmailAddress())).thenReturn(false);
        }
        when(conversationService.convertToUserList(userDtoList)).thenReturn(userList);
        when(userRepository.saveAll(userList)).thenReturn(userList);
        when(conversationService.convertToUserDtoList(userList)).thenReturn(userDtoList);

        List<UserDto> createdUsers = userService.createUserBulk(userDtoList);

        assertNotNull(createdUsers);
        assertEquals(userDtoList.size(), createdUsers.size());
    }

    @Test
    void shouldThrowExceptionWhenAnyUserAlreadyExists() {
        when(userRepository.existsByEmailAddress(userDtoList.get(0).getEmailAddress())).thenReturn(true);

        CustomException thrown = assertThrows(
                CustomException.class,
                () -> userService.createUserBulk(userDtoList),
                "User already exists"
        );

        assertTrue(thrown.getMessage().contains(userDtoList.get(0).getEmailAddress()));
        assertEquals(HttpStatus.OK, thrown.getHttpStatus());
    }

    @Test
    void whenUsersExist_thenShouldReturnUserDtoList() {

        when(userRepository.findAll(Sort.by(Sort.Order.desc("createdDate")))).thenReturn(userList);
        when(conversationService.convertToUserDto(any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            UserDto dto = new UserDto();
            dto.setEmailAddress(user.getEmailAddress());
            return dto;
        });

        List<UserDto> result = userService.getAllUser();

        assertEquals(userDtoList.size(), result.size());
        assertEquals(userDtoList.get(0).getEmailAddress(), result.get(0).getEmailAddress());
        assertEquals(userDtoList.get(1).getEmailAddress(), result.get(1).getEmailAddress());
    }

    @Test
    void whenUserExists_thenShouldReturnUserDto() {

        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(conversationService.convertToUserDto(any(User.class))).thenReturn(userDto);
        UserDto result = userService.getUserByEmailAddress("user@example.com");

        assertEquals(userDto.getEmailAddress(), result.getEmailAddress());
    }

    @Test
    void whenUserDoesNotExist_thenShouldThrowCustomException() {

        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        CustomException thrown = assertThrows(
                CustomException.class,
                () -> userService.getUserByEmailAddress("nonexistent@example.com"),
                "No User found for the given emailAddress"
        );
        assertEquals(HttpStatus.OK, thrown.getHttpStatus());
    }

    @Test
    void testUpdateUserByEmail() {

        UserDto userDto = new UserDto();
        userDto.setUserName("New Name");
        userDto.setRole("New Role");
        userDto.setGroup("New Group");
        userDto.setBusinessUnitName("QA");
        User user = new User();

        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(conversationService.convertToUserDto(any(User.class))).thenReturn(userDto);

        userService.updateUserByEmail("test@example.com", userDto);

        assertEquals("New Name", user.getUserName());
        assertEquals("New Role", user.getRole());
    }

    @Test
    void testUpdateUserByEmailBlankStringData() {

        UserDto userDto = new UserDto();
        userDto.setUserName("");
        userDto.setRole("");
        userDto.setGroup("");
        userDto.setBusinessUnitName("");
        User user = new User();

        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(conversationService.convertToUserDto(any(User.class))).thenReturn(userDto);

        userService.updateUserByEmail("test@example.com", userDto);

        assertNull(user.getUserName());
        assertNull(user.getRole());
    }
    @Test
    void testUpdateUserByEmail_UserNotFound() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.updateUserByEmail("test@example.com", new UserDto()));
    }


    @Test
    void testGetAllUserEmpty() {

        when(userRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        assertThrows(CustomException.class, () -> userService.getAllUser());
    }

    @Test
    void whenDeactivateActiveUser_thenShouldReturnTrue() {
        User user = new User();
        user.setEmailAddress("user1@example.com");
        user.setIsActive(true); // User is initially active
        when(userRepository.findByEmailAddress("user1@example.com")).thenReturn(Optional.of(this.user));

        userService.deactivateUserByEmail("user1@example.com");

        assertFalse(this.user.getIsActive());

    }

    @Test
    void testDeactivateUserByEmail_UserNotFound() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> userService.deactivateUserByEmail("test@example.com"));
    }

    @Test
    void testDeactivateUserByEmail_UserAlreadyInactive() {
        User user = new User();
        user.setIsActive(false);
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));

        assertThrows(CustomException.class, () -> userService.deactivateUserByEmail("test@example.com"));
    }

    @Test
    void loginUser() throws CloneNotSupportedException {
        String userName = "John";
        String password = "Sri@12345";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.ofNullable(user));
        LoginDto loginDto = new LoginDto();
        loginDto.setLoginMessage("login success");
        loginDto.setLastLoginTime("11-12-2023||12:10:12");
        loginDto.setRole("accosiate");
        when(conversationService.toLoginDto(user)).thenReturn(loginDto);
        LoginDto response = userService.loginUser(userName, password);
        assertEquals(response, loginDto);

    }
    @Test
    void loginUserFailedLoginInvalidPassword() throws CloneNotSupportedException {
        String userName = "John";
        String password = "InvalidPassword";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.ofNullable(user));
        LoginDto response = userService.loginUser(userName, password);
        assertNotNull(response);
        assertEquals("login failed", response.getLoginMessage());
    }

    @Test
    void loginUserExceptionUserNotFound() {
        String userName = "John";
        String password = "Sri@12345";
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.loginUser(userName, password));

    }

        @Test
        void testCreateOrUpdateUser() throws CloneNotSupportedException {
        UserDto userDto = new UserDto();
        User user = new User();
        user.setBusinessUnitName("FS");
        user.setEmailAddress("Durga1948");
        user.setGroup("Group1");
        user.setId(1L);
        user.setIsActive(true);
        user.setOid(UUID.randomUUID());
        user.setRole("admin");
        user.setUserFirstAndLastName("Durga");
        user.setUserName("Durgadevi");
        Instant originalLastLoginTime = user.getLastLoginTime();
        Optional<User> userOptional = Optional.of(user);
        when(userRepository.findByOid(userDto.getOid())).thenReturn(userOptional);
        when(conversationService.convertToUser(userDto)).thenReturn(user.clone());
        User result = userService.createOrUpdateUser(userDto);
        verify(userRepository, times(1)).findByOid(userDto.getOid());
        verify(userRepository, times(1)).save(user);
        assertNotNull(result);
}

    @Test
    void testCreateOrUpdateUserUserDoesNotExist() throws CloneNotSupportedException {
        UserDto userDto = new UserDto();
        User newUser = new User();
        Instant currentInstant = Instant.now();
        when(userRepository.findByOid(userDto.getOid())).thenReturn(Optional.empty());
        when(conversationService.convertToUser(userDto)).thenReturn(newUser);
        when(userRepository.insert(newUser)).thenReturn(newUser);

        User result = userService.createOrUpdateUser(userDto);
        verify(userRepository, times(1)).findByOid(userDto.getOid());
        verify(userRepository, times(1)).insert(newUser);
        assertNotSame(currentInstant, newUser.getLastLoginTime());
        assertNotNull(result);
    }
}