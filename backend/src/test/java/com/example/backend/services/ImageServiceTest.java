package com.example.backend.services;

import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private ImageService imageService;

    @Test
    void shouldDoNothingOnValidImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        ReflectionTestUtils.invokeMethod(imageService, "validateFile", file);
    }

    @Test
    void shouldThrowOnValidateFileIfContentTypeIsNull() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(imageService, "validateFile", file));
    }

    @Test
    void shouldThrowOnValidateFileIfContentTypeNotInValidList() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(imageService, "validateFile", file));
    }

    @Test
    void shouldThrowOnValidateFileIfFilenameIsNull() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(imageService, "validateFile", file));
    }

    @Test
    void shouldThrowOnValidateFileInFileExtensionIsNotInValidList() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        assertThrows(BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(imageService, "validateFile", file));
    }

    @Test
    void shouldThrowIfCanNotReadImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        assertThrows(BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(imageService, "validateFile", file));
    }

    @Test
    void shouldSave() throws Exception {
        String bucket = "test-bucket";

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getOriginalFilename()).thenReturn("test.jpg");
        when(image.getSize()).thenReturn(10L);

        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);

        when(image.getInputStream())
                .thenReturn(new ByteArrayInputStream(os.toByteArray()));

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(false);

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(null);

        String result = imageService.save(bucket, image);

        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));

        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldReturnNullIfImageIsNull() {
        String result = imageService.save("test-bucket", null);

        assertNull(result);
    }

    @Test
    void shouldReturnNullIfImageIsEmpty() {
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true);

        String result = imageService.save("test-bucket", image);

        assertNull(result);
    }

    @Test
    void shouldCreateBucketOnSaveIfNotExists() throws Exception {
        String bucket = "test-bucket";

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getOriginalFilename()).thenReturn("test.jpg");
        when(image.getSize()).thenReturn(10L);

        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);

        when(image.getInputStream())
                .thenReturn(new ByteArrayInputStream(os.toByteArray()));

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(false);

        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(null);

        String result = imageService.save(bucket, image);

        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));

        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldThrowOnSaveIfUploadFails() throws Exception {
        String bucket = "test-bucket";

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getOriginalFilename()).thenReturn("test.jpg");
        when(image.getSize()).thenReturn(10L);

        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);

        when(image.getInputStream())
                .thenReturn(new ByteArrayInputStream(os.toByteArray()));

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO failed"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> imageService.save(bucket, image)
        );

        assertTrue(ex.getMessage().contains("Upload image error"));

        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldGetPresignedUrl() throws
            ServerException,
            InsufficientDataException,
            ErrorResponseException,
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException,
            XmlParserException,
            InternalException {
        String bucket = "test-bucket";
        String fileName = "image.jpg";
        String url = "https://minio/presigned-url";

        doReturn(url)
                .when(minioClient)
                .getPresignedObjectUrl(any());

        String result = imageService.getPresignedUrl(bucket, fileName);

        assertEquals(url, result);

        verify(minioClient).getPresignedObjectUrl(any());
    }

    @Test
    void shouldThrowOnGetPresignedUrlIfNotFound() throws
            ServerException,
            InsufficientDataException,
            ErrorResponseException,
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException,
            XmlParserException,
            InternalException {
        String bucket = "test-bucket";
        String fileName = "image.jpg";

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> imageService.getPresignedUrl(bucket, fileName));

        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void shouldDeleteOne() throws Exception {
        String bucket = "test-bucket";
        String filename = "image.jpg";

        doNothing()
                .when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        imageService.delete(bucket, filename);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void shouldThrowOnDelete() throws Exception {
        String bucket = "test-bucket";
        String filename = "image.jpg";

        doThrow(new RuntimeException("minio failed"))
                .when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> imageService.delete(bucket, filename)
        );

        assertTrue(ex.getMessage().contains("Delete image error"));

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void shouldDeleteMany() {
        String bucket = "test-bucket";
        List<String> filenames = List.of("a.jpg", "b.jpg");

        Iterable<Result<DeleteError>> results = Collections.emptyList();

        when(minioClient.removeObjects(any(RemoveObjectsArgs.class)))
                .thenReturn(results);

        imageService.deleteMany(bucket, filenames);

        verify(minioClient).removeObjects(any(RemoveObjectsArgs.class));
    }

    @Test
    void shouldThrowOnDeleteMany() {
        String bucket = "test-bucket";
        List<String> filenames = List.of("a.jpg", "b.jpg");

        when(minioClient.removeObjects(any(RemoveObjectsArgs.class)))
                .thenThrow(new RuntimeException("minio failed"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> imageService.deleteMany(bucket, filenames)
        );

        assertTrue(ex.getMessage().contains("Delete many images error"));

        verify(minioClient).removeObjects(any(RemoveObjectsArgs.class));
    }

}
