package com.julytus.EBook.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenResponse implements Serializable {
    String userId;
    String accessToken;
}
