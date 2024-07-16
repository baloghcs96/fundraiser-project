package hu.progmasters.fundraiser.config;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CloudinaryConfigTest {

    @Autowired
    private Cloudinary cloudinary;

    @Test
    public void cloudinaryBeanShouldNotBeNull() {
        assertNotNull(cloudinary);
    }

}