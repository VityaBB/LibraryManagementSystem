package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String address;
    private String registrationDate;
    private Boolean isActive;
    private String role;
}