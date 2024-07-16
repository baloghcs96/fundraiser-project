package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<Exchange, Long> {

    Exchange findFirstByOrderByLocalDateTimeDesc();

    void deleteAll();
}
