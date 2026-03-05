package com.avega.taxgap.service;

import com.avega.taxgap.dto.LoginRequestDto;
import com.avega.taxgap.dto.SignUpRequestDto;
import com.avega.taxgap.exception.UserRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRequestValidation {
    public List<UserRequestException> validateUserRequest(SignUpRequestDto requestDto) {
        List<UserRequestException> errorExceptions = new ArrayList<>();
        if (requestDto.getName() == null || requestDto.getName().isEmpty()){
            UserRequestException errorException = new UserRequestException("name","UserRequest name should not be null");
            errorExceptions.add(errorException);
        }
        if (requestDto.getCity() == null || requestDto.getCity().isEmpty()){
            UserRequestException errorException = new UserRequestException("city","UserRequest city should not be null");
            errorExceptions.add(errorException);
        }
        if (requestDto.getEmail() == null || requestDto.getEmail().isEmpty()){
            UserRequestException errorException = new UserRequestException("email","UserRequest email should not be null");
            errorExceptions.add(errorException);
        }
        if (requestDto.getPassword() == null || requestDto.getPassword().isEmpty()){
            UserRequestException errorException = new UserRequestException("password","UserRequest password should not be null");
            errorExceptions.add(errorException);
        }
        if (requestDto.getPhoneNumber() == null || requestDto.getPhoneNumber().isEmpty()){
            UserRequestException errorException = new UserRequestException("phoneNumber","UserRequest phoneNumber should not be null");
            errorExceptions.add(errorException);
        }
        return errorExceptions;
    }

    public List<UserRequestException> loginRequestValidation(LoginRequestDto loginRequestDto) {
        List<UserRequestException> errorExceptions = new ArrayList<>();
        if (loginRequestDto.getEmail()==null || loginRequestDto.getEmail().isEmpty()){
            UserRequestException errorException = new UserRequestException("email","UserRequest email should not be null");
            errorExceptions.add(errorException);
        }
        if (loginRequestDto.getPassword()==null || loginRequestDto.getPassword().isEmpty()){
            UserRequestException errorException = new UserRequestException("password","UserRequest password should not be null");
            errorExceptions.add(errorException);
        }
        return errorExceptions;
    }
}
