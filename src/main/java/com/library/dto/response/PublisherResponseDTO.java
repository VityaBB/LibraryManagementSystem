package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;
}