package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Income;
import hu.progmasters.fundraiser.repository.IncomeRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;

    private FundService fundService;

    @Scheduled(fixedDelay = 60000)
    public void calculateAndSaveIncomeForCompletedFunds() {
        List<Fund> completedFunds = fundService.findCompletedFundsNotProcessedWithCommission();
        completedFunds.forEach(fund -> {
            double commission = fundService.calculateCommission(fund.getGoalAmount());

            Income income = new Income();
            income.setAmount(commission);
            income.setFund(fund);
            income.setCurrency(fund.getCurrency());
            income.setConfirmTime(LocalDateTime.now());

            incomeRepository.save(income);

            fund.setCompleted(true);
        });
    }

}
