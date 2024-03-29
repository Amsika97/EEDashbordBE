package com.maveric.digital.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.User;
import com.maveric.digital.responsedto.LoginDto;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.UserDto;
import com.maveric.digital.responsedto.UserFilterDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.UserService;
import com.maveric.digital.service.UserServiceImpl;
import jakarta.validation.ConstraintViolationException;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockBean
  UserService userService;
  @MockBean
  ConversationService conversationService;
  @Autowired
  UserController userController;
  public UserDto userDto;
  private List<UserDto> userDtoList;
  private List<User> userList;
  @BeforeEach
  private void initiateUserDtoData() {
    userDto = new UserDto();

    userDto.setUserName("John");
    userDto.setEmailAddress("john@example.com");
    userDto.setGroup("business");
    userDto.setBusinessUnitName("internal");
    userDto.setRole("accosiate");
    userDto.setIsActive(true);
    userDto.setOid(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));

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
  void createUser() throws Exception {
    when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
    mockMvc.perform(post("/v1/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userName", is(userDto.getUserName())))
        .andExpect(jsonPath("$.businessUnitName",
            is(userDto.getBusinessUnitName())));
  }

  @Test
  void createUserBulk() throws Exception {
    when(userService.createUserBulk(anyList())).thenReturn(userDtoList);
    mockMvc.perform(post("/v1/user/create/bulk")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDtoList)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void ThrowingConstraintViolationExceptionForCreateUser() throws Exception {
    when(userService.createUser(any(UserDto.class))).thenThrow(
        ConstraintViolationException.class);
    mockMvc.perform(post("/v1/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ConstraintViolationException));
  }


  @Test
  void ThrowingDataIntegrityViolationExceptionForCreateUser() throws Exception {
    when(userService.createUser(any(UserDto.class))).thenThrow(
        DataIntegrityViolationException.class);
    mockMvc.perform(post("/v1/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof DataIntegrityViolationException));

  }

  @Test
  void ThrowingResourceCreationExceptionForCreateUser() throws Exception {
    when(userService.createUser(any(UserDto.class))).thenThrow(
        ResourceCreationException.class);
    mockMvc.perform(post("/v1/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ResourceCreationException));

  }

  @Test
  void getUserByEmailAddress() throws Exception {
    var userDto = new UserDto();
    List<UserDto> userDtos = new ArrayList<>();
    userDtos.add(userDto);
    when(userService.getUserByEmailAddress(anyString())).thenReturn(userDto);
    mockMvc.perform(get("/v1/user/email/{emailAddress}", "john@example.com")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAllUsers() throws Exception {
    List<UserDto> userDtos = new ArrayList<>();
    userDtos.add(new UserDto());

    when(userService.getAllUser()).thenReturn(userDtos);
    mockMvc.perform(get("/v1/user/all")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateUserByEmail() throws Exception {
    when(userService.updateUserByEmail(("john@example.com"), (userDto))).thenReturn(userDto);

    mockMvc.perform(put("/v1/user/update/{emailAddress}", "john@example.com")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDto)))
        .andExpect(status().isOk());
  }

  @Test
   void shouldDeactivateUser() throws Exception {
    String emailAddress = "john.doe@example.com";
    doNothing().when(userService).deactivateUserByEmail(emailAddress);

    mockMvc.perform(put("/v1/user/deactivate/{emailAddress}", emailAddress)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect((ResultMatcher) content().string(
            "User with email address '" + emailAddress + "' has been deactivated."));

  }

  @Test
  void loginUser() throws Exception {
    LoginDto loginDto = new LoginDto();
    String userName="sri";
    String password="123";
    when(userService.loginUser(userName,password)).thenReturn(loginDto);
    mockMvc.perform(post("/v1/user/login/{userName}/{password}",userName,password)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }


  private String asJsonString(Object obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(obj);
  }

  @Test
  void testCreateOrUpdate() throws CloneNotSupportedException {
    User user = new User();
    user.setBusinessUnitName("FS");
    user.setCreatedDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    user.setEmailAddress("durga1948");
    user.setGroup("Group1");
    user.setId(1L);
    user.setIsActive(true);
    user.setLastLoginTime(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    UUID oid = UUID.randomUUID();
    user.setOid(oid);
    user.setRole("admin");
    user.setUserFirstAndLastName("Durga");
    user.setUserName("Durgadevi");
    Optional<User> ofResult = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.save(Mockito.<User>any())).thenReturn(user);
    when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(ofResult);
    UserServiceImpl usersService = new UserServiceImpl(userRepository, new ConversationService());
    UserController userController = new UserController(usersService, new ConversationService());
    ResponseEntity<UserDto> actualCreateOrUpdateResult = userController.createOrUpdate(new UserDto());
    verify(userRepository).findByOid(Mockito.<UUID>any());
    verify(userRepository).save(Mockito.<User>any());
    UserDto userDto1 = actualCreateOrUpdateResult.getBody();
    assertEquals("durga1948", userDto1.getEmailAddress());
    assertEquals("FS", userDto1.getBusinessUnitName());
    assertEquals("Group1", userDto1.getGroup());
    assertEquals("Durga", userDto1.getUserFirstAndLastName());
    assertEquals("admin", userDto1.getRole());
    assertEquals("Durgadevi", userDto1.getUserName());
    assertEquals(1L, userDto1.getId().longValue());
    assertEquals(200, actualCreateOrUpdateResult.getStatusCodeValue());
    assertTrue(userDto1.getIsActive());
    assertTrue(actualCreateOrUpdateResult.hasBody());
    assertTrue(actualCreateOrUpdateResult.getHeaders().isEmpty());
    assertSame(oid, userDto1.getOid());
  }
  @Test
  void testUserDRopDownOptions() throws Exception {

    ResponseEntity<Map<String,List<UserFilterDto>>> response = userController.getUserForDropDown();

    mockMvc.perform(get("/v1/users/dropdown-options")).andExpect(status().isOk())
            .andExpect(content().contentType("application/json")).andReturn();
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
