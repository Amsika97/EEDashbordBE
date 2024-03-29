package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFilterDto {
    private UUID oid;
    private String name;
    private String role;
}
