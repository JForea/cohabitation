package com.example.backend.services;

import com.example.backend.exceptions.BadRequestException;
import com.example.backend.intefaces.FileStorage;
import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    private final MinioClient minioClient;

    public ImageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        String exceptionText = "Only .jpg, .png and .webp file types allowed.";

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp")
                )) {
            throw new BadRequestException(exceptionText);
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.matches(".*\\.(jpg|jpeg|png|webp)")
        ) {
            throw new BadRequestException(exceptionText);
        }

        try (InputStream input = file.getInputStream()) {
            BufferedImage image = ImageIO.read(input);

            if (image == null) {
                throw new BadRequestException(exceptionText);
            }
        } catch (IOException e) {
            throw new BadRequestException(exceptionText);
        }
    }

    public String save(String bucket, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            validateFile(image);

            try {
                String filename = UUID.randomUUID() + "-" + image.getOriginalFilename();

                boolean exists = minioClient.bucketExists(
                        BucketExistsArgs.builder().bucket(bucket).build()
                );

                if (!exists)
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .stream(image.getInputStream(), image.getSize(), -1)
                                .contentType(image.getContentType())
                                .build()
                );

                return filename;
            } catch (Exception e) {
                log.error("Upload image error: ", e);
                throw new RuntimeException("Upload image error.", e);
            }
        }

        return null;
    }

    public void delete(String bucket, String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.
                            builder().
                            bucket(bucket).
                            object(filename).
                            build()
            );
        } catch (Exception e) {
            log.error("Delete image error: ", e);
            throw new RuntimeException("Delete image error.", e);
        }
    }
}
