package hu.progmasters.fundraiser.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hu.progmasters.fundraiser.domain.entity.Image;
import hu.progmasters.fundraiser.exception.CloudinaryException;
import hu.progmasters.fundraiser.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@AllArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;


    public List<Image> uploadImages(List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Image> images = new ArrayList<>();
        AtomicReference<Map> uploadResult = new AtomicReference<>();

        imageFiles.forEach(file -> {
            if (file != null && !file.isEmpty()) {
                try {
                    uploadResult.set(cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()));
                    String imageUrl = (String) uploadResult.get().get("secure_url");
                    Image image = new Image(imageUrl, file.getOriginalFilename());
                    images.add(image);
                    imageRepository.save(image);
                } catch (IOException e) {
                    throw new CloudinaryException("Error uploading file");
                }
            }
        });

        return images;
    }
}





