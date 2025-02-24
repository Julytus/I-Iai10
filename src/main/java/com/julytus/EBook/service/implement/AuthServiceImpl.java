package com.julytus.EBook.service.implement;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.julytus.EBook.dto.request.LogoutRequest;
import com.julytus.EBook.dto.request.SignInRequest;
import com.julytus.EBook.dto.response.RefreshTokenResponse;
import com.julytus.EBook.dto.response.SignInResponse;
import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.exception.JwtAuthenticationException;
import com.julytus.EBook.model.User;
import com.julytus.EBook.service.AuthService;
import com.julytus.EBook.service.JwtService;
import com.julytus.EBook.service.RedisService;
import com.julytus.EBook.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthServiceImpl implements AuthService {
    @Value("${DOMAIN:localhost}")
    private String domain;

    private final JwtService jwtService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final UserService userService;

    @Value("${jwt.secret-key-refresh-token}")
    private String secretKeyRefreshToken;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;


    @Override
    public SignInResponse signIn(SignInRequest request, HttpServletResponse response) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(authentication.getName());

            final String accessToken = jwtService.generateAccessToken(user);
            final String refreshToken = jwtService.generateRefreshToken(user);

            Cookie cookie = createRefreshTokenCookie(refreshToken);
            response.addCookie(cookie);

            return SignInResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            throw new JwtAuthenticationException(ErrorCode.UNAUTHORIZED);
        }
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setMaxAge(expirationRefreshToken);
        return cookie;
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userService.findByUsername(username);

        try {
            boolean isValidToken = jwtService.verificationToken(refreshToken, secretKeyRefreshToken);
            if (!isValidToken) {
                throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
            String accessToken = jwtService.generateAccessToken(user);
            log.info("refresh token success");
            return RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .userId(user.getId())
                    .build();
        } catch (ParseException | JOSEException e) {
            log.error("Error while refresh token");
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public void logout(LogoutRequest request, HttpServletResponse response) {
        long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        if(accessTokenExp > 0) {
            try {
                String jwtId = SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
                redisService.save(jwtId, request.getAccessToken(), accessTokenExp, TimeUnit.MILLISECONDS);
                deleteRefreshTokenCookie(response);
            } catch (ParseException e) {
                throw new AppException(ErrorCode.SIGN_OUT_FAILED);
            }
        }
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
