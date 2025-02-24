package com.julytus.EBook.service;

import com.julytus.EBook.dto.request.SignUpRequest;
import com.julytus.EBook.dto.request.UpdateUserRequest;
import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse createUser(SignUpRequest request);
    UserResponse updateUserInfo(UpdateUserRequest request);
    UserResponse updateAvatar(MultipartFile file);
    User findByUsername(String email);
}
