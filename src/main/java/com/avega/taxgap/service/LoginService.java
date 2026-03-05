package com.avega.taxgap.service;

import com.avega.taxgap.dto.APIResponse;
import com.avega.taxgap.dto.LoginRequestDto;
import com.avega.taxgap.dto.SignUpRequestDto;
import com.avega.taxgap.entity.User;
import com.avega.taxgap.exception.BadRequestException;
import com.avega.taxgap.exception.UserRequestException;
import com.avega.taxgap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final UserRequestValidation userRequestValidation;
    private final LoginUtil loginUtil;

    public APIResponse signUpUser(SignUpRequestDto requestDto) {
        List<UserRequestException> errorExceptions = userRequestValidation.validateUserRequest(requestDto);
        if (!errorExceptions.isEmpty()){
            throw new BadRequestException("User parameter is invalid",errorExceptions);
        }
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setCity(requestDto.getCity());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user =userRepository.save(user);
        return new APIResponse(HttpStatus.OK.value(),user,null);
    }

    public APIResponse login(LoginRequestDto loginRequestDto) {
        List<UserRequestException> errorExceptions = userRequestValidation.loginRequestValidation(loginRequestDto);
        if (!errorExceptions.isEmpty()) {
            throw new BadRequestException("User parameter is invalid", errorExceptions);
        }
        User user=userRepository.findOneByEmailAndPassword(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        if (user == null){
            return new APIResponse(HttpStatus.BAD_REQUEST.value(),null,"User is invalid");
        }
        String token =loginUtil.generateToken(user);
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken",token);
        return new APIResponse(HttpStatus.OK.value(),data,null);
    }
}
