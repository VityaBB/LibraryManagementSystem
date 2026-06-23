package com.library.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherUpdateDTO {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;
}