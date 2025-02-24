package com.julytus.EBook.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MinIoConfig {
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @Value("${minio.cover-image-bucket}")
    private String coverImageBucket;

    @Value("${minio.chapter-bucket}")
    private String chapterBucket;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build();

        try {
            // Tạo và set policy cho avatar bucket
            createBucketIfNotExists(minioClient, avatarBucket);
            setPublicPolicy(minioClient, avatarBucket);

            // Tạo và set policy cho cover image bucket
            createBucketIfNotExists(minioClient, coverImageBucket);
            setPublicPolicy(minioClient, coverImageBucket);

            // Tạo và set policy cho chapter bucket
            createBucketIfNotExists(minioClient, chapterBucket);
            setPublicPolicy(minioClient, chapterBucket);
        } catch (Exception e) {
            log.error("Error initializing MinIO buckets: {}", e.getMessage());
        }

        return minioClient;
    }

    private void createBucketIfNotExists(MinioClient minioClient, String bucket) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private void setPublicPolicy(MinioClient minioClient, String bucket) throws Exception {
        String policy = String.format("""
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"AWS": "*"},
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                    }
                ]
            }
            """, bucket);

        minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(bucket)
                .config(policy)
                .build()
        );
    }
}
