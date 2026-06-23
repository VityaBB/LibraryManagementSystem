package com.library.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCreateDTO {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String biography;
}