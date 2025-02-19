package com.julytus.EBook.exception;

import com.julytus.EBook.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class GlobalHandlingException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {

        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errors = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errors.size() > 1 ? String.valueOf(errors) : errors.get(0))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ProblemDetail> handleIdentityException(AppException exception, HttpServletRequest request) {

        ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle("Application Exception");
        problemDetail.setType(URI.create(request.getRequestURI()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("errors", List.of(exception.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

//    {
//        "title": "Application Exception",
//            "status": 400,
//            "detail": "Invalid user ID",
//            "type": "/api/users/123",
//            "instance": "/api/users/123",
//            "errors": ["Invalid user ID"]
//    }
}
