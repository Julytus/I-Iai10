package com.julytus.EBook.service;

import com.julytus.EBook.model.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String accessToken);
    boolean verificationToken(String token, String secretKey) throws ParseException, JOSEException;
    long extractTokenExpired(String token);
}
