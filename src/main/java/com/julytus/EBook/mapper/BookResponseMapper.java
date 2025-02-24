package com.julytus.EBook.mapper;

import com.julytus.EBook.dto.response.BookResponse;
import com.julytus.EBook.model.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public class BookResponseMapper {
    public static BookResponse fromBook(Book book) {
        return BookResponse
                .builder()
                .id(book.getId())
                .views(book.getViews())
                .managedBy(book.getManagedBy().getFullName())
                .author(book.getAuthor())
                .coverImage(book.getCoverImage())
                .description(book.getDescription())
                .title(book.getTitle())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static List<BookResponse> fromPageBook(Page<Book> books) {
        return books.getContent().stream()
                .map(BookResponseMapper::fromBook).toList();
    }
}
