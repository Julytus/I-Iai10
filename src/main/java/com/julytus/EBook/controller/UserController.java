package com.julytus.EBook.controller;

import com.julytus.EBook.dto.request.UpdateUserRequest;
import com.julytus.EBook.dto.response.ResponseData;
import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @PutMapping("/user")
    ResponseData<UserResponse> updateUserProfile(
            @RequestBody @Valid UpdateUserRequest request
    ) {
        var result = userService.updateUserInfo(request);
        return ResponseData.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Update user profile successfully!")
                .data(result)
                .build();
    }

    @PutMapping("/user/avatar")
    ResponseData<UserResponse> updateAvatar(
            @RequestBody MultipartFile file
    ) {
        var result = userService.updateAvatar(file);
        return ResponseData.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Update avatar successfully!")
                .data(result)
                .build();
    }
}
