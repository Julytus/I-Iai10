package com.julytus.EBook.service;

import com.julytus.EBook.dto.request.BookCreationRequest;
import com.julytus.EBook.dto.response.BookResponse;
import com.julytus.EBook.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookResponse createBook(MultipartFile coverImage, BookCreationRequest request);
    BookResponse updateBook(MultipartFile coverImage, BookCreationRequest request);
    Void updateCoverImage(MultipartFile coverImage, String bookId);
    PageResponse<BookResponse> getAllBook(int page, int size);
}
