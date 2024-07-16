package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.enumeration.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {

    @Query("SELECT f from Fund f WHERE f.category = :category")
    List<Fund> findAllByCategory(@Param("category") Category category);

    @Query("SELECT f from Fund f WHERE f.title = :title")
    List<Fund> findAllByTitle(@Param("title") String title);

    @Query("SELECT f FROM Fund f WHERE f.completedDate IS NOT NULL AND f.isCompleted = FALSE")
    List<Fund> findAllByCompletedDateIsNotNullAndIsCompletedFalse();

    List<Fund> findAllByOpenForDonationTrue();

}
