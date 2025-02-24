package com.julytus.EBook.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInResponse implements Serializable {
    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("user_id")
    String userId;
}
