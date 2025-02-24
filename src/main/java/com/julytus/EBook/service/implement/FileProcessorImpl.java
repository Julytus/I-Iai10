package com.julytus.EBook.service.implement;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.julytus.EBook.exception.AppException;
import com.julytus.EBook.exception.ErrorCode;
import com.julytus.EBook.service.FileProcessor;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "FILE-PROCESSOR")
@RequiredArgsConstructor
public class FileProcessorImpl implements FileProcessor {
    private final MinioClient minioClient;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @Value("${minio.cover-image-bucket}")
    private String coverImageBucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Override
    public String uploadAvatar(MultipartFile file, String username) {
        String fileName = username + "/" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                + "_" + UUID.randomUUID();

        return uploadImageToMinio(file, avatarBucket, fileName);
    }

    @Override
    public String uploadCoverImage(MultipartFile file, String title) {
        String fileName =title + "/" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                + "_" + UUID.randomUUID();

        return uploadImageToMinio(file, coverImageBucket, fileName);
    }

    @Override
    public String uploadChapter(List<MultipartFile> files) {
        return "";
    }

    private String uploadImageToMinio(MultipartFile file, String bucket, String fileName) {
        try {
            validateImageFile(file);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Trả về URL public
            return String.format("%s/%s/%s", minioUrl, bucket, fileName);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
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
