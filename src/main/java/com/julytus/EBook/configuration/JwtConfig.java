package com.julytus.EBook.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import java.text.ParseException;

@Configuration
@Slf4j(topic = "JWT-DECODER")
public class JwtConfig {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    @Value("${jwt.secret-key-access-token}")
    private String secretKeyAccessToken;

    @Value("${jwt.secret-key-refresh-token}")
    private String secretKeyRefreshToken;

    private SecretKey getSecretKey(String secretKey) {
        byte[] keyBytes = Base64.from(secretKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
    //ACCESS TOKEN DECODER, ENCODER
    @Bean
    @Primary
    public JwtDecoder accessTokenJwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(secretKeyAccessToken))
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        return getJwtDecoder(jwtDecoder);
    }

    @Bean
    @Primary
    public JwtEncoder accessTokenJwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(secretKeyAccessToken)));
    }

    //REFRESH TOKEN DECODER, ENCODER
    @Bean
    public JwtDecoder refreshTokenJwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(secretKeyRefreshToken))
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        return getJwtDecoder(jwtDecoder);
    }

    @Bean
    public JwtEncoder refreshTokenJwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(secretKeyRefreshToken)));
    }

    private JwtDecoder getJwtDecoder(NimbusJwtDecoder jwtDecoder) {
        return token -> {
            try {
                boolean isValid = true; // check BlackList
                if(isValid) {
                    return jwtDecoder.decode(token);
                }
            } catch (Exception e) {
                log.error("Jwt decoder: Token invalid");
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            throw new JwtException("Invalid token");
        };
    }
}