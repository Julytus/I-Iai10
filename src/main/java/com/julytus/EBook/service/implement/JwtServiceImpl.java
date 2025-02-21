package com.julytus.EBook.service.implement;

import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.model.User;
import com.julytus.EBook.service.JwtService;
import com.julytus.EBook.service.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.julytus.EBook.configuration.JwtConfig.JWT_ALGORITHM;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder accessTokenJwtEncoder;
    private final JwtEncoder refreshTokenJwtEncoder;
    private final RedisService redisService;

    public JwtServiceImpl(
            @Qualifier("accessTokenJwtEncoder") JwtEncoder accessTokenJwtEncoder,
            @Qualifier("refreshTokenJwtEncoder") JwtEncoder refreshTokenJwtEncoder,
            RedisService redisService
//            @Qualifier("refreshTokenJwtDecoder") JwtDecoder refreshTokenJwtDecoder,
            ) {
        this.accessTokenJwtEncoder = accessTokenJwtEncoder;
        this.refreshTokenJwtEncoder = refreshTokenJwtEncoder;
        this.redisService = redisService;
//        this.refreshTokenJwtDecoder = refreshTokenJwtDecoder;
    }

    @Value("${jwt.expiration-access-token}")
    private int expirationAccessToken;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Override
    public String generateAccessToken(User user) {
        try {
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(expirationAccessToken, ChronoUnit.SECONDS))
                    .subject(user.getEmail())
                    .claim("username", user.getUsername())
                    .claim("role", user.getRole().getName())
                    .id(UUID.randomUUID().toString())
                    .build();
            JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
            return accessTokenJwtEncoder
                    .encode(JwtEncoderParameters.from(jwsHeader, claims))
                    .getTokenValue();
        } catch (JwtEncodingException e) {
            throw new JwtEncodingException("Cannot create jwt token, error :" + e.getMessage());
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        try {
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(expirationRefreshToken, ChronoUnit.SECONDS))
                    .subject(user.getId())
                    .build();
            JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
            return refreshTokenJwtEncoder
                    .encode(JwtEncoderParameters.from(jwsHeader, claims))
                    .getTokenValue();
        } catch (JwtEncodingException e) {
            throw new JwtEncodingException("Cannot create jwt token, error :" + e.getMessage());
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
    public boolean verificationToken(String token, String secretKey)
            throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(StringUtils.isNotBlank(redisService.get(jwtId))) {
            throw new AppException(ErrorCode.TOKEN_BLACK_LIST);
        }

        var expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expiration.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        return signedJWT.verify(new MACVerifier(secretKey));
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
