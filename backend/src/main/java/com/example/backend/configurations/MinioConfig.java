package com.example.backend.configurations;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient() {
        String minioUrl = System.getenv("MINIO_URL");
        String minioRootUser = System.getenv("MINIO_ROOT_USER");
        String minioRootPassword = System.getenv("MINIO_ROOT_PASSWORD");

        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioRootUser, minioRootPassword)
                .build();
    }
}
