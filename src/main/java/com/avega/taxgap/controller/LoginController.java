package com.avega.taxgap.controller;

import com.avega.taxgap.dto.APIResponse;
import com.avega.taxgap.dto.LoginRequestDto;
import com.avega.taxgap.dto.SignUpRequestDto;
import com.avega.taxgap.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tax")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    //sign up the user details
    @PostMapping("/sign-up")
    public ResponseEntity<APIResponse> signUpUser(@RequestBody SignUpRequestDto requestDto){
        APIResponse apiResponse = loginService.signUpUser(requestDto);
        return ResponseEntity.
                status(apiResponse.getStatus())
                .body(apiResponse);
    }

    //login using correct email and password
    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequestDto loginRequestDto){
        APIResponse apiResponse = loginService.login(loginRequestDto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
