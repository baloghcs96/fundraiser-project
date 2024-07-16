package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
}
