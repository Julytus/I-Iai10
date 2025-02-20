package com.julytus.EBook.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.julytus.EBook.common.Gender;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest implements Serializable {
    private String fullName;

    private String phoneNumber;

    private Integer age;

    private Gender gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
