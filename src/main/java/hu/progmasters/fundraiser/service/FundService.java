package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Image;
import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.dto.incoming.FundSaveCommand;
import hu.progmasters.fundraiser.dto.incoming.FundUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundUpdateInfo;
import hu.progmasters.fundraiser.event.OnGoalCompletedEvent;
import hu.progmasters.fundraiser.exception.DateTooFarException;
import hu.progmasters.fundraiser.exception.FundNotFoundByCategoryException;
import hu.progmasters.fundraiser.exception.FundNotFoundByIdException;
import hu.progmasters.fundraiser.exception.FundNotFoundByTitleException;
import hu.progmasters.fundraiser.repository.FundRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FundService {

    private static final double COMMISSION = 0.03;
    private final ModelMapper modelMapper;
    private final FundRepository fundRepository;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageService imageService;

    public FundInfo saveFund(FundSaveCommand command, Authentication authentication) {
        Long accountId = accountService.getLoggedInUserId(authentication);
        Account account = accountService.findAccountById(accountId);
        List<Image> images = imageService.uploadImages(command.getImages());
        Fund fund = modelMapper.map(command, Fund.class);
        checkAndSetCloseDate(fund);
        setGoalAmountWithCommission(fund, command.getGoalAmount());
        fund.setImages(images);
        images.forEach(image -> image.setFund(fund));
        fund.setAccount(account);
        fundRepository.save(fund);
        return modelMapper.map(fund, FundInfo.class);
    }

    private void checkAndSetCloseDate(Fund fund) {
        LocalDateTime today = LocalDateTime.now();
        if (fund.getCloseDate() != null) {
            int monthBetween = (int) ChronoUnit.MONTHS.between(today, fund.getCloseDate());
            if (monthBetween >= 12) {
                throw new DateTooFarException(monthBetween);
            }
        }
        if (fund.getCloseDate() == null) {
            fund.setCloseDate(LocalDateTime.now().plusMonths(12));
        }
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void checkAndCloseFunds() {
        List<Fund> openFunds = fundRepository.findAllByOpenForDonationTrue();
        LocalDateTime today = LocalDateTime.now();
        openFunds.forEach(fund -> {
            if (fund.getCloseDate().isBefore(today)) {
                setCloseForDonationFalse(fund);
            }
        });
    }

    private void setCloseForDonationFalse(Fund fund) {
        //Ha késöbb lesz methode amivel kitudja szedni az adományokat és manuálisan zárni a fundot akkor ez a metódus kell majd oda is
        fund.setOpenForDonation(false);
    }

    public Fund findFundById(Long fundId) {
        return fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundByIdException(fundId));
    }

    public List<FundInfo> findAllByCategory(Category category) throws FundNotFoundByCategoryException {
        List<Fund> funds = fundRepository.findAllByCategory(category);
        if (funds.isEmpty()) {
            throw new FundNotFoundByCategoryException(category);
        }
        return funds.stream()
                .map(fund -> modelMapper.map(fund, FundInfo.class))
                .collect(Collectors.toList());
    }

    public List<FundInfo> findAllByTitle(String title) throws FundNotFoundByTitleException {
        List<Fund> funds = fundRepository.findAllByTitle(title);
        if (funds.isEmpty()) {
            throw new FundNotFoundByTitleException(title);
        }
        return funds.stream()
                .map(fund -> modelMapper.map(fund, FundInfo.class))
                .collect(Collectors.toList());
    }

    public void isFundGoalCompleted(Fund fund) {
        if (fund.getGoalAmount() <= fund.getCurrentAmount() && fund.getCompletedDate() == null) {
            fundGoalCompleted(fund);
        }
    }

    private void fundGoalCompleted(Fund fund) {
        FundCompletedInfo fundCompletedInfo = new FundCompletedInfo();
        fundCompletedInfo.setFundTitle(fund.getTitle());
        fundCompletedInfo.setSubject("Goal completed");
        fund.setCompletedDate(LocalDateTime.now());
        List<String> participantsEmails = fund.getFundTransactions().stream()
                .map(transaction -> transaction.getSenderAccount().getEmail())
                .distinct()
                .collect(Collectors.toList());
        participantsEmails.forEach(email -> {
            fundCompletedInfo.setEmail(email);
            publishEventSendFundGoalCompletedEmail(fundCompletedInfo, "message.fundGoalReachedParticipant");
        });
        fundCompletedInfo.setEmail(fund.getAccount().getEmail());
        publishEventSendFundGoalCompletedEmail(fundCompletedInfo, "message.fundGoalReachedOwner");
    }

    private void publishEventSendFundGoalCompletedEmail(FundCompletedInfo fundCompletedInfo, String text) {
        eventPublisher.publishEvent(new OnGoalCompletedEvent(fundCompletedInfo, text));
    }

    public FundInfo getFundInfoById(Long fundId) {
        Fund fund = findFundById(fundId);
        FundInfo fundInfo = modelMapper.map(fund, FundInfo.class);
        setFundProgress(fundInfo);
        return fundInfo;
        //Törölve a felesleges mappelés?
    }

    private void setFundProgress(FundInfo fundInfo) {
        fundInfo.setProgress((fundInfo.getCurrentAmount() / fundInfo.getGoalAmount()) * 100);
    }

    public double calculateCommission(double amount) {
        return amount * COMMISSION;
    }

    private void setGoalAmountWithCommission(Fund fund, double amount) {
        double commission = calculateCommission(amount);
        fund.setGoalAmount(amount + commission);
    }

    public List<Fund> findCompletedFundsNotProcessedWithCommission() {
        return fundRepository.findAllByCompletedDateIsNotNullAndIsCompletedFalse();
    }

    public FundUpdateInfo updateFund(Long fundId, FundUpdateCommand command) {
        Fund existingFund = findFundById(fundId);
        existingFund.setTitle(command.getTitle());
        existingFund.setDescription(command.getDescription());

        if (command.getImages() != null && !command.getImages().isEmpty()) {
//            existingFund.getImages().clear();
            List<Image> images = imageService.uploadImages(command.getImages());
            existingFund.setImages(images);
            images.forEach(image -> image.setFund(existingFund));
        }

//        fundRepository.save(existingFund);

        return modelMapper.map(existingFund, FundUpdateInfo.class);

    }
}
