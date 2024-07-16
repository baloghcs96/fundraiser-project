package hu.progmasters.fundraiser.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import hu.progmasters.fundraiser.domain.entity.Image;
import hu.progmasters.fundraiser.exception.CloudinaryException;
import hu.progmasters.fundraiser.repository.ImageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private ImageRepository imageRepository;

    private ImageService imageService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        imageService = new ImageService(cloudinary, imageRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void uploadImages_withNullInput_shouldReturnEmptyList() {
        List<Image> result = imageService.uploadImages(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void uploadImages_withEmptyList_shouldReturnEmptyList() {
        List<Image> result = imageService.uploadImages(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void uploadImages_withValidFiles_shouldReturnNonEmptyList() throws IOException {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        when(file1.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file2.getBytes()).thenReturn(new byte[]{4, 5, 6});
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");

        Map uploadResult = Map.of("secure_url", "http://example.com/image1.jpg");
        when(cloudinary.uploader()).thenReturn(mock(Uploader.class));
        when(cloudinary.uploader().upload(any(byte[].class), any(Map.class))).thenReturn(uploadResult);

        List<MultipartFile> files = List.of(file1, file2);

        List<Image> result = imageService.uploadImages(files);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("http://example.com/image1.jpg", result.get(0).getUrl());
        verify(imageRepository, times(2)).save(any(Image.class));
    }


    @Test
    void uploadImages_withIOException_shouldThrowCloudinaryException() throws IOException {
        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getBytes()).thenThrow(new IOException());

        List<MultipartFile> files = Collections.singletonList(file1);

        assertThrows(CloudinaryException.class, () -> imageService.uploadImages(files));
    }

}