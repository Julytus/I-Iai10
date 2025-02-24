package com.julytus.EBook.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
}
