package com.julytus.EBook.service.implement;

import com.julytus.EBook.annotation.IsManager;
import com.julytus.EBook.dto.request.BookCreationRequest;
import com.julytus.EBook.dto.response.BookResponse;
import com.julytus.EBook.dto.response.PageResponse;
import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.exception.JwtAuthenticationException;
import com.julytus.EBook.model.User;
import com.julytus.EBook.service.BookService;
import com.julytus.EBook.service.FileProcessor;
import com.julytus.EBook.service.UserService;
import com.julytus.EBook.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.julytus.EBook.repository.BookRepository;
import com.julytus.EBook.model.Book;
import com.julytus.EBook.mapper.BookResponseMapper;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "BOOK-SERVICE")
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final FileProcessor fileProcessor;
    private final UserService userService;

    @Override
    @IsManager
    public BookResponse createBook(MultipartFile coverImage,BookCreationRequest request) {
        String username = SecurityUtil.getCurrentLogin()
                .orElseThrow(() -> new JwtAuthenticationException(ErrorCode.UNAUTHORIZED));

        User user = userService.findByUsername(username);

        String coverImageUrl = fileProcessor.uploadAvatar(coverImage, username);

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .description(request.getDescription())
                .managedBy(user)
                .coverImage(coverImageUrl)
                .build();
        
        return BookResponseMapper.fromBook(bookRepository.save(book));
    }

    @Override
    @IsManager
    public BookResponse updateBook(MultipartFile coverImage, BookCreationRequest request) {
        return null;
    }

    @Override
    @IsManager
    public Void updateCoverImage(MultipartFile coverImage, String bookId) {
        Book book = getBookById(bookId);

        String newCoverImageUrl = fileProcessor.uploadCoverImage(coverImage, book.getTitle());
        book.setCoverImage(newCoverImageUrl);

        bookRepository.save(book);
        return null;
    }

    @Override
    public PageResponse<BookResponse> getAllBook(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        return PageResponse.<BookResponse>builder()
                .currentPage(page)
                .pageSize(bookPage.getSize())
                .totalPages(bookPage.getTotalPages())
                .totalElements(bookPage.getTotalElements())
                .data(BookResponseMapper.fromPageBook(bookPage))
                .build();
    }

    private Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
    }
}
