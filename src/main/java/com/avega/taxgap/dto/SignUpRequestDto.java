package com.avega.taxgap.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String name;
    private String email;
    private String password;
    private String city;
    private String phoneNumber;
}
