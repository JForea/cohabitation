package com.example.backend.intefaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String save(String bucket, MultipartFile image);
    void delete(String bucket, String filename);
}
