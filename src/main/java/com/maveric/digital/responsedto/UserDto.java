package com.maveric.digital.responsedto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 5093283713070442562L;
  private Long id;
  @NotBlank(message = "Username is required")
  private String userName;
  private String name;
  @NotBlank(message = "Email address is required")
  @Email(message = "Invalid email address format")
  private String emailAddress;
  private String userFirstAndLastName;
  @NotNull(message = "oid is required")
  private UUID oid;
  private Instant createdDate;
  private String lastLoginTime;
  private Boolean isActive;
  @NotBlank(message = "Role is required")
  private  String role;
  @NotBlank(message = "Group is required")
  private String group;
  @NotBlank(message = "Business unit name is required")
  private String businessUnitName;

}
