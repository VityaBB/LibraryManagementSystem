package com.library.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String role;
}