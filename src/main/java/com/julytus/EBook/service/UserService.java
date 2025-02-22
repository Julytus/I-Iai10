package com.julytus.EBook.service;

import com.julytus.EBook.dto.request.UserRequest;
import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(UserRequest request, MultipartFile avatar);
    User findByEmail(String email);
}
