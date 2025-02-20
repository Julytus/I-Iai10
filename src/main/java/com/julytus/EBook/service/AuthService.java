package com.julytus.EBook.service;

import com.julytus.EBook.dto.request.LogoutRequest;
import com.julytus.EBook.dto.request.SignInRequest;
import com.julytus.EBook.dto.response.RefreshTokenResponse;
import com.julytus.EBook.dto.response.SignInResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

public interface AuthService {
    SignInResponse SignIn(SignInRequest request, HttpServletResponse response);
    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken);
    void logout(LogoutRequest request, HttpServletResponse response);
}
