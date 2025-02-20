package com.julytus.EBook.service.implement;

import com.julytus.EBook.dto.request.LogoutRequest;
import com.julytus.EBook.dto.request.SignInRequest;
import com.julytus.EBook.dto.response.RefreshTokenResponse;
import com.julytus.EBook.dto.response.SignInResponse;
import com.julytus.EBook.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;

public class AuthServiceImpl implements AuthService {
    @Override
    public SignInResponse SignIn(SignInRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void logout(LogoutRequest request, HttpServletResponse response) {

    }
}
