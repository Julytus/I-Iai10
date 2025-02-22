package com.julytus.EBook.exception;

import java.net.URI;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.julytus.EBook.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalHandlingException {
    private static final EnumMap<JwtExceptionType, ErrorCode> JWT_ERROR_MAPPING;
    
    static {
        JWT_ERROR_MAPPING = new EnumMap<>(JwtExceptionType.class);
        JWT_ERROR_MAPPING.put(JwtExceptionType.EXPIRED, ErrorCode.JWT_EXPIRED);
        JWT_ERROR_MAPPING.put(JwtExceptionType.MALFORMED, ErrorCode.JWT_MALFORMED);
        JWT_ERROR_MAPPING.put(JwtExceptionType.SIGNATURE, ErrorCode.JWT_SIGNATURE_INVALID);
        JWT_ERROR_MAPPING.put(JwtExceptionType.UNSUPPORTED, ErrorCode.JWT_UNSUPPORTED);
    }

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
        problemDetail.setTitle("Error");
        problemDetail.setType(URI.create(request.getRequestURI()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("errors", List.of(exception.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleJwtAuthenticationException(
            JwtAuthenticationException exception, HttpServletRequest request) {
        
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getCode())
                .error(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(
            SecurityException exception, HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Security error: " + exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class) 
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception exception, HttpServletRequest request) {
            
        // Log exception để debug
        log.error("Handling exception: ", exception);
        
        ErrorCode errorCode = getErrorCode(exception);
        return buildErrorResponse(errorCode, request);
    }

    private ErrorCode getErrorCode(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName();
        
        try {
            JwtExceptionType jwtExceptionType = JwtExceptionType.valueOf(
                exceptionName.replace("Exception", "").toUpperCase()
            );
            return JWT_ERROR_MAPPING.getOrDefault(jwtExceptionType, ErrorCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return ErrorCode.UNAUTHORIZED;
        }
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            ErrorCode errorCode, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getCode())
                .error(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}
