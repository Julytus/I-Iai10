package com.julytus.EBook.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Builder
public class BookResponse {
    String id;

    String title;

    String description;

    @JsonProperty("cover_image")
    String coverImage;

    String author;

    Long views;

    @JsonProperty("managed_by")
    String managedBy;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
