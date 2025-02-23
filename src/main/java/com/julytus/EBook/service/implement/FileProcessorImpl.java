package com.julytus.EBook.service.implement;

import java.util.Objects;
import java.util.UUID;

import com.julytus.EBook.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.service.FileProcessor;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j(topic = "FILE-PROCESSOR")
@RequiredArgsConstructor
public class FileProcessorImpl implements FileProcessor {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            validateImageFile(file);

            String fileName = UUID.randomUUID() + "_"
                    + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @PostConstruct
    private void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", e.getMessage());
            throw new AppException(ErrorCode.STORAGE_INITIALIZATION_FAILED);
        }
    }

    private static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE);
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }
}
