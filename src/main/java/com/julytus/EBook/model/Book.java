package com.julytus.EBook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book extends BaseEntity<String> {
    @Column(nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    String publisher;

    @Column(nullable = false)
    String coverImage;

    @ColumnDefault("0")
    private Long views;

    @ManyToOne(optional = false)
    private User author;
}
