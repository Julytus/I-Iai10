package com.julytus.EBook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class BookCreationRequest implements Serializable {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
