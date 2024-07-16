package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}
