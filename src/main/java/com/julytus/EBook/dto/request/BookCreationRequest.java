package com.julytus.EBook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class BookCreationRequest implements Serializable {
    @NotBlank(message = "Title cannot be blank")
    String title;

    @NotBlank(message = "Description cannot be blank")
    String description;

    String author;
}
