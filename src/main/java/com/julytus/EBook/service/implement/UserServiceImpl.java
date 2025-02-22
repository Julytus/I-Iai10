package com.julytus.EBook.service.implement;

import com.julytus.EBook.common.PredefinedRole;
import com.julytus.EBook.common.UserStatus;
import com.julytus.EBook.dto.request.UserRequest;
import com.julytus.EBook.dto.response.UserResponse;
import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.mapper.UserResponseMapper;
import com.julytus.EBook.model.Role;
import com.julytus.EBook.model.User;
import com.julytus.EBook.repository.RoleRepository;
import com.julytus.EBook.repository.UserRepository;
import com.julytus.EBook.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists {}", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findByName(PredefinedRole.USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .userStatus(UserStatus.ACTIVE)
                .role(role)
                .build();

        return UserResponseMapper.fromUser(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(UserRequest request, MultipartFile avatar) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
