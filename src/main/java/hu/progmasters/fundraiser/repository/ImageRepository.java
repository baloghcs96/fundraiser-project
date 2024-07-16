package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
