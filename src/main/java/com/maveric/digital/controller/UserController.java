package com.maveric.digital.controller;
import com.maveric.digital.responsedto.LoginDto;
import com.maveric.digital.responsedto.UserDto;
import com.maveric.digital.responsedto.UserFilterDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserController {


  private final UserService usersService;
  private final ConversationService conversationService;

  @PostMapping("/user/create")
  @Operation(description = "Create User")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created a user successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto usersDto)
      throws URISyntaxException {
    log.debug("UserController::createUser()::Start ");
    log.debug("UserDto {}", usersDto);
    UserDto userDtoRes = usersService.createUser(usersDto);
    log.debug("UserController::createUser()::Ended ");
    return ResponseEntity.created(new URI("/v1/user/create/" + usersDto.getId())).body(userDtoRes);

  }

  @PostMapping("/user/create/bulk")
  @Operation(description = "Create Users in bulk")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created users successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
  public ResponseEntity<List<UserDto>> createUserBulk(
      @RequestBody @Valid List<UserDto> usersDtoList)
      throws URISyntaxException {
    log.debug("UserController:: createUsers() call started");
    log.debug("UserDtoList{}", usersDtoList);
    List<UserDto> usersDtoListRes = usersService.createUserBulk(usersDtoList);
    log.debug("UserController:: createUsers() call ended");
    return ResponseEntity.created(new URI("/v1/user/create/bulk/")).body(usersDtoListRes);

  }

  @PutMapping("/user/update/{emailAddress}")
  @Operation(description = "Update Users by Email Address")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated the user successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")
  })
  public ResponseEntity<UserDto> updateUserByEmail(@PathVariable String emailAddress,
      @RequestBody UserDto userDto) {
    log.debug("UsersController:: updateUserByEmail() call started");
    UserDto updatedUser = usersService.updateUserByEmail(emailAddress, userDto);
    log.debug("UserController::updateUserByEmail() call ended");
    return ResponseEntity.ok().body((updatedUser));
  }

  @GetMapping("/user/all")
  @Operation(description = "Get all Users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get all users successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
  public ResponseEntity<List<UserDto>> getAllUsers() {
    log.debug("UsersController::getAllUsers() call started");
    List<UserDto> userDtoListRes = usersService.getAllUser();
    log.debug("UsersController::getAllUsers() call ended");
    return ResponseEntity.ok(userDtoListRes);
  }

  @GetMapping("/user/email/{emailAddress}")
  @Operation(description = "Get User by Email")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Get user successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
  public ResponseEntity<UserDto> getUserByEmailAddress(@PathVariable String emailAddress) {
    log.debug("UsersController:: getUserByEmailAddress() call started");
    UserDto userDto = usersService.getUserByEmailAddress(emailAddress);
    log.debug("UsersController:: getUserByEmailAddress() call ended");
    return ResponseEntity.ok().body(userDto);
  }

  @PutMapping("/user/deactivate/{emailAddress}")
  @Operation(description = "Deactivate user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deactivated user successfully"),
      @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
  public ResponseEntity<String> deactivateUserByEmail(@PathVariable String emailAddress) {
    log.debug("UsersController:: deactivateUserByEmail() call started");
    usersService.deactivateUserByEmail(emailAddress);
    log.debug("UsersController:: deactivateUserByEmail() call ended");
    return ResponseEntity.ok("User with email address '" + emailAddress + "' has been deactivated.");
  }
  @PostMapping("/user/login/{userName}/{password}")
  @Operation(description = "user login")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "user login success"),
          @ApiResponse(responseCode = "400", description = "user login failed")})
  public ResponseEntity<LoginDto> login(@PathVariable String userName, @PathVariable String password ) throws CloneNotSupportedException {
    log.debug("UsersController:: login() call started");
    return ResponseEntity.ok(usersService.loginUser(userName,password));
  }
  
  @PostMapping("/user/create/update")
  @Operation(description = "user create or update")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "user create or update success"),
          @ApiResponse(responseCode = "400", description = "user create or update failed")})
  public ResponseEntity<UserDto> createOrUpdate(@RequestBody UserDto userRequest) throws CloneNotSupportedException {
    log.debug("UsersController:: createOrUpdate() call started");
    return ResponseEntity.ok(conversationService.convertToUserDto(usersService.createOrUpdateUser(userRequest)));
  }
  @Operation(
          summary = "Get users having user/reviewer roles for the dropdown"
  )
  @ApiResponses(value = {@ApiResponse(
          responseCode = "200",
          description = "Returns all users having user and reviewer role"
  )})
  @GetMapping(path="/users/dropdown-options")
  public ResponseEntity<Map<String,List<UserFilterDto>>> getUserForDropDown(){
    log.debug("UserController::getUserForDropDown() call started");
    return ResponseEntity.ok(usersService.getUsersForDropDown());
  }
  @Operation(
          summary = "Get all users for the dropdown"
  )
  @ApiResponses(value = {@ApiResponse(
          responseCode = "200",
          description = "Returns all users "
  )})
  @GetMapping(path="/users/all/dropdown-options")
  public ResponseEntity<List<UserFilterDto>> GetAllUserForDropdown(){
    log.info("UserController::GetAllUserForDropdown()");
    return  ResponseEntity.ok(usersService.getListOfUsersForDropDown());
  }


}
