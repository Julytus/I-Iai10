package com.julytus.EBook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UpdateBookRequest {
    @NotBlank(message = "Title cannot be blank")
    String title;

    @NotBlank(message = "Description cannot be blank")
    String description;
}
