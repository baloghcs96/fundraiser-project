package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Badge;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByGrade(Grade grade);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO badge (image_url, grade) VALUES " +
            "('https://res.cloudinary.com/dqwjmfaio/image/upload/v1719949905/ufzl7pmh1lnk2lnbbq2n.jpg', 'BRONZE')," +
            "('https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950253/tkugpekzetamzkr2asc5.png', 'SILVER')," +
            "('https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950877/qpr0cdhelvmjfcgjsjgg.png', 'GOLD')," +
            "('https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950902/ioiqwvucbfkdqwcwaw56.png', 'PLATINUM')", nativeQuery = true)
    void insertMultipleBadges();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO badge (image_url, grade) VALUES (:imageUrl, :grade)", nativeQuery = true)
    void insertBadge(String imageUrl, String grade);

}
