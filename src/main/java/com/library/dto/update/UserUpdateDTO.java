package com.library.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String role;
    private Boolean isActive;
}