package com.example.backend.intefaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String save(String bucket, MultipartFile image);
    String getPresignedUrl(String bucket, String fileName);
    void delete(String bucket, String filename);
}
