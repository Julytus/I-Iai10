package com.julytus.EBook.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvVariable {
    // Getters
    @Getter
    private static String secretKeyAccessToken;
    @Getter
    private static String secretKeyRefreshToken;
    @Getter
    private static int expirationAccessToken;
    @Getter
    private static int expirationRefreshToken;

    @Value("${jwt.secret-key-access-token}")
    public void setSecretKeyAccessToken(String value) {
        secretKeyAccessToken = value;
    }

    @Value("${jwt.secret-key-refresh-token}")
    public void setSecretKeyRefreshToken(String value) {
        secretKeyRefreshToken = value;
    }

    @Value("${jwt.expiration-access-token}")
    public void setExpirationAccessToken(int value) {
        expirationAccessToken = value;
    }

    @Value("${jwt.expiration-refresh-token}")
    public void setExpirationRefreshToken(int value) {
        expirationRefreshToken = value;
    }

}
