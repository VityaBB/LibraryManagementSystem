package com.library.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorUpdateDTO {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String biography;
}