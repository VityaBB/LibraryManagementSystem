package com.library.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherCreateDTO {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;
}