package com.julytus.EBook.service.implement;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.stereotype.Service;

import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.model.User;
import com.julytus.EBook.service.JwtService;
import com.julytus.EBook.service.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "JWT-SERVICE")
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final RedisService redisService;

    @Value("${jwt.secret-key-access-token}")
    private String secretKeyAccessToken;

    @Value("${jwt.expiration-access-token}")
    private int expirationAccessToken;

    @Value("${jwt.secret-key-refresh-token}")
    private String secretKeyRefreshToken;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Override
    public String generateAccessToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("JulyTus")
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now()
                            .plus(expirationAccessToken, ChronoUnit.SECONDS).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("role", user.getRole().getName())
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(new MACSigner(secretKeyAccessToken));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while signing JWT token: {}", e.getMessage());
            throw new JwtEncodingException("Failed to generate JWT token");
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("JulyTus")
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now()
                            .plus(expirationRefreshToken, ChronoUnit.SECONDS).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(new MACSigner(secretKeyRefreshToken));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new JwtEncodingException("Failed to generate JWT token");
        }
    }

    @Override
    public String extractUsername(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean verificationToken(String token, String secretKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (StringUtils.isNotBlank(redisService.get(jwtId))) {
                throw new AppException(ErrorCode.TOKEN_BLACK_LIST);
            }

            var expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.before(new Date())) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }

            return signedJWT.verify(new MACVerifier(secretKey));

        } catch (ParseException | JOSEException e) {
            log.error("Failed to verify JWT token: {}", e.getMessage());
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean inBlackList(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (StringUtils.isNotBlank(redisService.get(jwtId))) {
                throw new AppException(ErrorCode.TOKEN_BLACK_LIST);
            }
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        return false;
    }

    @Override
    public long extractTokenExpired(String token) {
        try {
            long expirationTime = SignedJWT.parse(token)
                    .getJWTClaimsSet().getExpirationTime().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime - currentTime, 0);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
}
