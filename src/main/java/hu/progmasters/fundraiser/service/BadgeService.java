package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Badge;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.dto.outgoing.BadgeInfo;
import hu.progmasters.fundraiser.exception.BadgeAlreadyExistException;
import hu.progmasters.fundraiser.exception.GradeNotFoundException;
import hu.progmasters.fundraiser.repository.BadgeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;

@Service
@Transactional
@AllArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;


    public void insertBronzeBadge() {
        BadgeInfo badgeInfo = new BadgeInfo();
        checkTheBadgeAlreadyExist(Grade.BRONZE);
        badgeRepository.insertBadge("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719949905/ufzl7pmh1lnk2lnbbq2n.jpg", "BRONZE");
        badgeInfo.setMessage("Bronze badge saved successfully");
    }

    public void insertSilverBadge() {
        BadgeInfo badgeInfo = new BadgeInfo();
        checkTheBadgeAlreadyExist(Grade.SILVER);
        badgeRepository.insertBadge("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950253/tkugpekzetamzkr2asc5.png", "SILVER");
        badgeInfo.setMessage("Silver badge saved successfully");
    }

    public void insertGoldBadge() {
        BadgeInfo badgeInfo = new BadgeInfo();
        checkTheBadgeAlreadyExist(Grade.GOLD);
        badgeRepository.insertBadge("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950877/qpr0cdhelvmjfcgjsjgg.png", "GOLD");
        badgeInfo.setMessage("Gold badge saved successfully");

    }

    public void insertPlatinumBadge() {
        BadgeInfo badgeInfo = new BadgeInfo();
        checkTheBadgeAlreadyExist(Grade.PLATINUM);
        badgeRepository.insertBadge("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950902/ioiqwvucbfkdqwcwaw56.png", "PLATINUM");
        badgeInfo.setMessage("Platinum badge saved successfully");
    }

    public void initializeBadgesIfNeeded() {
        Arrays.stream(Grade.values())
                .filter(grade -> badgeRepository.findByGrade(grade).isEmpty())
                .forEach(this::insertBadgeForGrade);
    }

    private void insertBadgeForGrade(Grade grade) {
        switch (grade) {
            case BRONZE:
                insertBronzeBadge();
                break;
            case SILVER:
                insertSilverBadge();
                break;
            case GOLD:
                insertGoldBadge();
                break;
            case PLATINUM:
                insertPlatinumBadge();
                break;
            default:
                throw new IllegalArgumentException("Unsupported grade: " + grade);
        }
    }

    public Badge getBadgeByGrade(Grade grade) throws GradeNotFoundException {
        return badgeRepository.findByGrade(grade).orElseThrow(() -> new GradeNotFoundException(grade));
    }

    private void checkTheBadgeAlreadyExist(Grade grade) {
        if (badgeRepository.findByGrade(grade).isPresent()) {
            throw new BadgeAlreadyExistException(grade);
        }

    }

}
