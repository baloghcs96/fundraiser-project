package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Badge;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.exception.BadgeAlreadyExistException;
import hu.progmasters.fundraiser.exception.GradeNotFoundException;
import hu.progmasters.fundraiser.repository.BadgeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void insertBronzeBadge_whenBadgeDoesNotExist_shouldInsertBadge() {
        when(badgeRepository.findByGrade(Grade.BRONZE)).thenReturn(Optional.empty());

        badgeService.insertBronzeBadge();

        verify(badgeRepository, times(1))
                .insertBadge(eq("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719949905/ufzl7pmh1lnk2lnbbq2n.jpg"), eq("BRONZE"));
    }

    @Test
    void insertBronzeBadge_whenBadgeExists_shouldThrowException() {
        when(badgeRepository.findByGrade(Grade.BRONZE)).thenReturn(Optional.of(new Badge()));

        assertThrows(BadgeAlreadyExistException.class, () -> badgeService.insertBronzeBadge());

        verify(badgeRepository, never()).insertBadge(anyString(), anyString());
    }

    @Test
    void insertSilverBadge_whenBadgeDoesNotExist_shouldInsertBadge() {
        when(badgeRepository.findByGrade(Grade.SILVER)).thenReturn(Optional.empty());

        badgeService.insertSilverBadge();

        verify(badgeRepository, times(1))
                .insertBadge(eq("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950253/tkugpekzetamzkr2asc5.png"), eq("SILVER"));
    }

    @Test
    void insertSilverBadge_whenBadgeExists_shouldThrowException() {
        when(badgeRepository.findByGrade(Grade.SILVER)).thenReturn(Optional.of(new Badge()));

        assertThrows(BadgeAlreadyExistException.class, () -> badgeService.insertSilverBadge());

        verify(badgeRepository, never()).insertBadge(anyString(), anyString());
    }

    @Test
    void insertGoldBadge_whenBadgeDoesNotExist_shouldInsertBadge() {
        when(badgeRepository.findByGrade(Grade.GOLD)).thenReturn(Optional.empty());

        badgeService.insertGoldBadge();

        verify(badgeRepository, times(1))
                .insertBadge(eq("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950877/qpr0cdhelvmjfcgjsjgg.png"), eq("GOLD"));
    }

    @Test
    void insertGoldBadge_whenBadgeExists_shouldThrowException() {
        when(badgeRepository.findByGrade(Grade.GOLD)).thenReturn(Optional.of(new Badge()));

        assertThrows(BadgeAlreadyExistException.class, () -> badgeService.insertGoldBadge());

        verify(badgeRepository, never()).insertBadge(anyString(), anyString());
    }

    @Test
    void insertPlatinumBadge_whenBadgeDoesNotExist_shouldInsertBadge() {
        when(badgeRepository.findByGrade(Grade.PLATINUM)).thenReturn(Optional.empty());

        badgeService.insertPlatinumBadge();

        verify(badgeRepository, times(1))
                .insertBadge(eq("https://res.cloudinary.com/dqwjmfaio/image/upload/v1719950902/ioiqwvucbfkdqwcwaw56.png"), eq("PLATINUM"));
    }

    @Test
    void insertPlatinumBadge_whenBadgeExists_shouldThrowException() {
        when(badgeRepository.findByGrade(Grade.PLATINUM)).thenReturn(Optional.of(new Badge()));

        assertThrows(BadgeAlreadyExistException.class, () -> badgeService.insertPlatinumBadge());

        verify(badgeRepository, never()).insertBadge(anyString(), anyString());
    }

    @Test
    void initializeBadgesIfNeeded_shouldInitializeMissingBadges() {
        when(badgeRepository.findByGrade(Grade.BRONZE)).thenReturn(Optional.empty());
        when(badgeRepository.findByGrade(Grade.SILVER)).thenReturn(Optional.of(new Badge()));
        when(badgeRepository.findByGrade(Grade.GOLD)).thenReturn(Optional.empty());
        when(badgeRepository.findByGrade(Grade.PLATINUM)).thenReturn(Optional.of(new Badge()));

        badgeService.initializeBadgesIfNeeded();

        verify(badgeRepository, times(1)).insertBadge(anyString(), eq("BRONZE"));
        verify(badgeRepository, never()).insertBadge(anyString(), eq("SILVER"));
        verify(badgeRepository, times(1)).insertBadge(anyString(), eq("GOLD"));
        verify(badgeRepository, never()).insertBadge(anyString(), eq("PLATINUM"));
    }

    @Test
    void getBadgeByGrade_whenBadgeExists_returnsBadge() throws GradeNotFoundException {
        Badge expectedBadge = new Badge();
        when(badgeRepository.findByGrade(Grade.GOLD)).thenReturn(Optional.of(expectedBadge));

        Badge result = badgeService.getBadgeByGrade(Grade.GOLD);

        assertEquals(expectedBadge, result);
    }

    @Test
    void getBadgeByGrade_whenBadgeDoesNotExist_throwsException() {
        when(badgeRepository.findByGrade(Grade.GOLD)).thenReturn(Optional.empty());

        assertThrows(GradeNotFoundException.class, () -> badgeService.getBadgeByGrade(Grade.GOLD));
    }

}