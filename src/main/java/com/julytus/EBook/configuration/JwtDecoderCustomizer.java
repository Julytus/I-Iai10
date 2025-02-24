package com.julytus.EBook.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.service.JwtService;
import com.julytus.EBook.service.RedisService;
import com.nimbusds.jose.JWSAlgorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "CUSTOM-JWT-DECODER")
@Component
@RequiredArgsConstructor
public class JwtDecoderCustomizer implements JwtDecoder {

    private final RedisService redisService;
    private NimbusJwtDecoder nimbusJwtDecoder;
    private final JwtService jwtService;

    @Value("${jwt.secret-key-access-token}")
    private String secretKeyAccessToken;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKey key = new SecretKeySpec(
                secretKeyAccessToken.getBytes(), 
                JWSAlgorithm.HS256.getName()
            );
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
        try {
            if (!jwtService.inBlackList(token)) {
                return nimbusJwtDecoder.decode(token);
            }
        } catch (Exception e) {
            log.error("Jwt decoder: Token invalid - {}", e.getMessage());
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        throw new JwtException("Invalid token");
    }
}
