package com.julytus.EBook.configuration;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.julytus.EBook.common.EnvVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.exception.JwtAuthenticationException;
import com.julytus.EBook.service.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "CUSTOM-JWT-DECODER")
@Component
@RequiredArgsConstructor
public class JwtDecoderCustomizer implements JwtDecoder {

    private final RedisService redisService;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            // Verify token signature
            JWSVerifier verifier = new MACVerifier(EnvVariable.getSecretKeyAccessToken());
            if (!signedJWT.verify(verifier)) {
                throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
            }

            // Check token in blacklist
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (StringUtils.isNotBlank(redisService.get(jwtId))) {
                throw new JwtAuthenticationException(ErrorCode.TOKEN_BLACK_LIST);
            }

            // Check token expiration
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.before(new Date())) {
                throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
            }

            // Extract claims and build JWT
            return buildJwt(token, signedJWT);

        } catch (ParseException | JOSEException e) {
            log.error("Failed to decode JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }
    }

    private Jwt buildJwt(String token, SignedJWT signedJWT) throws ParseException {
        Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
        return Jwt.withTokenValue(token)
                .header("alg", signedJWT.getHeader().getAlgorithm().getName())
                .claims(claimsSet -> claimsSet.putAll(claims))
                .build();
    }
}
