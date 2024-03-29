package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String loginMessage;
    private String lastLoginTime;
    private String userName;
    private String emailAddress;
    private String userFirstAndLastName;
    private UUID oid;
    private  String role;
    public LoginDto(String loginMessage) {
        this.loginMessage = loginMessage;
    }
}
