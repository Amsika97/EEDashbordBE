package com.maveric.digital.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(value = "users")
public class User extends IdentifiedEntity implements Serializable,Cloneable {

    @Serial
    private static final long serialVersionUID = 8173230593190715554L;
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
    private Instant lastLoginTime;
    private Boolean isActive;
    @NotBlank(message = "Role is required")
    private String role;
    private String group;
    private String businessUnitName;
    private String password;

	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}
}
