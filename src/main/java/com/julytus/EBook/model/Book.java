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

    @Column(nullable = false, columnDefinition = "VARCHAR(1000)")
    String coverImage;

    String author;

    @ColumnDefault("0")
    Long views;

    @ManyToOne(optional = false)
    User managedBy;
}
