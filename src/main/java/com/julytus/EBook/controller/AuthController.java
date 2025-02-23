package com.julytus.EBook.controller;

import com.julytus.EBook.dto.request.SignInRequest;
import com.julytus.EBook.dto.request.SignUpRequest;
import com.julytus.EBook.dto.response.ResponseData;
import com.julytus.EBook.dto.response.SignInResponse;
import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.service.AuthService;
import com.julytus.EBook.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/sign-in")
    ResponseData<SignInResponse> signIn(@RequestBody @Valid SignInRequest request,
                                        HttpServletResponse response) {
        var result = authService.signIn(request, response);
        return ResponseData.<SignInResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Sign in success")
                .data(result)
                .build();
    }

    @PostMapping("/sign-up")
    ResponseData<UserResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        var result = userService.createUser(request);

        return ResponseData.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created")
                .data(result)
                .build();
    }
}
