package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("SELECT f FROM FlashSale f WHERE f.gradeValue <= :gradeValue")
    List<FlashSale> findAllByGrade(@Param("gradeValue") int gradeValue);
}
