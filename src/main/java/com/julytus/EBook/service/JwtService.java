package com.julytus.EBook.service;

import java.text.ParseException;

import com.julytus.EBook.model.User;
import com.nimbusds.jose.JOSEException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String accessToken);
    boolean verificationToken(String token, String secretKey) throws ParseException, JOSEException;
    boolean inBlackList(String token);
    long extractTokenExpired(String token);
}
