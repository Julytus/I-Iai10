package com.julytus.EBook.mapper;

import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.model.User;

public class UserResponseMapper {
    public static UserResponse fromUser(User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .age(user.getAge())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .birthday(user.getBirthday())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
