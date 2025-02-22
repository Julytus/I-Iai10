package com.julytus.EBook.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.julytus.EBook.common.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Builder
public class UserResponse {
    String id;

    @JsonProperty("full_name")
    String fullName;

    String email;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;

    Integer age;

    @JsonProperty("avatar_url")
    String avatarUrl;
}
