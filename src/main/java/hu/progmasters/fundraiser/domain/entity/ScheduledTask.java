package hu.progmasters.fundraiser.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastRun;

    @Override
    public String toString() {
        return "ScheduledTask{" +
                "id=" + id +
                ", lastRun=" + lastRun +
                '}';
    }
}
