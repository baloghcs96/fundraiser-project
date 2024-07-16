package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Image;
import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.dto.incoming.FundSaveCommand;
import hu.progmasters.fundraiser.dto.incoming.FundUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FundInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundUpdateInfo;
import hu.progmasters.fundraiser.exception.DateTooFarException;
import hu.progmasters.fundraiser.exception.FundNotFoundByCategoryException;
import hu.progmasters.fundraiser.exception.FundNotFoundByIdException;
import hu.progmasters.fundraiser.exception.FundNotFoundByTitleException;
import hu.progmasters.fundraiser.repository.FundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FundServiceTest {

    @Mock
    private FundRepository fundRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private FundService fundService;

    private Fund fund;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fundService = new FundService(modelMapper, fundRepository, accountService, eventPublisher, imageService);
        fund = new Fund();
        fund.setGoalAmount(1000.0);
        fund.setCurrentAmount(1000);
        fund.setCompletedDate(null);
        fund.setFundTransactions(new ArrayList<>());
    }
/*
    @Test
    public void testSaveFund() {
        Long accountId = 1L;
        FundSaveCommand command = new FundSaveCommand();
        command.setGoalAmount(1000.0);
        List<Image> mockImages = Arrays.asList(new Image(), new Image());
        Account mockAccount = new Account();
        mockAccount.setAccountId(accountId);
        when(accountService.findAccountById(accountId)).thenReturn(mockAccount);
        when(imageService.uploadImages(command.getImages())).thenReturn(mockImages);
        Fund mockFund = new Fund();
        FundInfo mockFundInfo = new FundInfo();
        when(modelMapper.map(command, Fund.class)).thenReturn(mockFund);
        when(modelMapper.map(mockFund, FundInfo.class)).thenReturn(mockFundInfo);
        FundInfo result = fundService.saveFund(command, accountId);
        assertNotNull(result);
        assertEquals(mockFundInfo, result);
        assertEquals(accountId, mockFund.getAccount().getAccountId());
        assertEquals(mockImages.size(), mockFund.getImages().size());
        verify(fundRepository, times(1)).save(mockFund);
    }*/

    @Test
    void saveFund_withValidData_returnsFundInfo() {
        // Setup
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
        Account account = new Account();
        account.setAccountId(1L);
        when(accountService.findAccountById(1L)).thenReturn(account);
        List<Image> images = List.of(new Image());
        when(imageService.uploadImages(any())).thenReturn(images);
        FundSaveCommand command = new FundSaveCommand();
        command.setGoalAmount(1000.0);
        command.setImages(new ArrayList<>());
        Fund fund = new Fund();
        fund.setGoalAmount(1030.0); // Including commission
        when(modelMapper.map(command, Fund.class)).thenReturn(fund);
        when(modelMapper.map(fund, FundInfo.class)).thenReturn(new FundInfo());

        // Execute
        FundInfo result = fundService.saveFund(command, authentication);

        // Verify
        assertNotNull(result);
        verify(fundRepository, times(1)).save(fund);
    }

    @Test
    void saveFund_withFutureCloseDateBeyondLimit_throwsDateTooFarException() {
        // Setup
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
        Account account = new Account();
        account.setAccountId(1L);
        when(accountService.findAccountById(1L)).thenReturn(account);
        FundSaveCommand command = new FundSaveCommand();
        command.setGoalAmount(1000.0);
        Fund fund = new Fund();
        fund.setGoalAmount(1030.0); // Including commission
        fund.setCloseDate(LocalDateTime.now().plusMonths(13)); // Beyond the 12-month limit
        when(modelMapper.map(command, Fund.class)).thenReturn(fund);

        // Execute & Verify
        assertThrows(DateTooFarException.class, () -> fundService.saveFund(command, authentication));
    }

    @Test
    void saveFund_withCloseDateWithinLimit_savesFundSuccessfully() {
        // Setup
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
        Account account = new Account();
        account.setAccountId(1L);
        when(accountService.findAccountById(1L)).thenReturn(account);
        FundSaveCommand command = new FundSaveCommand();
        command.setGoalAmount(1000.0);
        Fund fund = new Fund();
        fund.setGoalAmount(1030.0); // Including commission
        fund.setCloseDate(LocalDateTime.now().plusMonths(6)); // Within the 12-month limit
        when(modelMapper.map(command, Fund.class)).thenReturn(fund);
        when(modelMapper.map(fund, FundInfo.class)).thenReturn(new FundInfo());

        // Execute
        FundInfo result = fundService.saveFund(command, authentication);

        // Verify
        assertNotNull(result);
        verify(fundRepository, times(1)).save(fund);
    }


    @Test
    public void testFindFundById() {
        Long fundId = 1L;
        Fund mockFund = new Fund();
        mockFund.setFundId(fundId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(mockFund));
        Fund result = fundService.findFundById(fundId);
        assertNotNull(result);
        assertEquals(fundId, result.getFundId());
        verify(fundRepository, times(1)).findById(fundId);
    }

    @Test
    public void testFindFundById_NotFound() {
        Long fundId = 1L;
        when(fundRepository.findById(fundId)).thenReturn(Optional.empty());
        assertThrows(FundNotFoundByIdException.class, () -> fundService.findFundById(fundId));
        verify(fundRepository, times(1)).findById(fundId);
    }

    @Test
    public void testFindAllByCategory() {
        Category category = Category.THEATER;
        Fund mockFund = new Fund();
        mockFund.setCategory(category);
        when(fundRepository.findAllByCategory(category)).thenReturn(List.of(mockFund));
        when(modelMapper.map(mockFund, FundInfo.class)).thenReturn(new FundInfo());
        List<FundInfo> result = fundService.findAllByCategory(category);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(fundRepository, times(1)).findAllByCategory(category);
    }

    @Test
    public void testFindAllByCategory_NotFound() {
        Category category = Category.THEATER;
        when(fundRepository.findAllByCategory(category)).thenReturn(Collections.emptyList());
        assertThrows(FundNotFoundByCategoryException.class, () -> fundService.findAllByCategory(category));
        verify(fundRepository, times(1)).findAllByCategory(category);
    }


    @Test
    public void testFindAllByTitle() {
        String title = "Education Fund";
        Fund mockFund = new Fund();
        mockFund.setTitle(title);
        when(fundRepository.findAllByTitle(title)).thenReturn(List.of(mockFund));
        when(modelMapper.map(mockFund, FundInfo.class)).thenReturn(new FundInfo());
        List<FundInfo> result = fundService.findAllByTitle(title);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(fundRepository, times(1)).findAllByTitle(title);
    }

    @Test
    public void testFindAllByTitle_NotFound() {
        String title = "Education Fund";
        when(fundRepository.findAllByTitle(title)).thenReturn(Collections.emptyList());
        assertThrows(FundNotFoundByTitleException.class, () -> fundService.findAllByTitle(title));
        verify(fundRepository, times(1)).findAllByTitle(title);
    }

    @Test
    public void testIsFundGoalCompleted_GoalCompleted() {
        Account account = new Account();
        account.setEmail("test@example.com");
        fund.setAccount(account);
        FundService spyFundService = Mockito.spy(fundService);
        spyFundService.isFundGoalCompleted(fund);
        verify(spyFundService, times(1)).isFundGoalCompleted(fund);
    }

    @Test
    void testIsFundGoalCompleted_GoalNotReached() {
        Fund fund = new Fund();
        fund.setGoalAmount(1000.0);
        fund.setCurrentAmount(0);
        FundService spyFundService = Mockito.spy(fundService);
        spyFundService.isFundGoalCompleted(fund);
        verify(spyFundService, times(1)).isFundGoalCompleted(fund);
    }

    @Test
    void testIsFundGoalCompleted_GoalAlreadyReached() {
        Fund fund = new Fund();
        fund.setGoalAmount(1000.0);
        fund.setCompletedDate(LocalDateTime.now());
        FundService spyFundService = Mockito.spy(fundService);
        spyFundService.isFundGoalCompleted(fund);
        verify(spyFundService, times(1)).isFundGoalCompleted(fund);
    }

    @Test
    void testGetFundInfoById() {
        Long fundId = 1L;
        Fund mockFund = new Fund();
        mockFund.setFundId(fundId);
        mockFund.setCurrentAmount(500);
        mockFund.setGoalAmount(1000.0);
        mockFund.setTitle("Test Fund");
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(mockFund));
        FundInfo expectedFundInfo = new FundInfo();
        expectedFundInfo.setTitle("Test Fund");
        expectedFundInfo.setGoalAmount(1000.0);
        expectedFundInfo.setCurrentAmount(500.0);
        expectedFundInfo.setProgress((expectedFundInfo.getCurrentAmount() / expectedFundInfo.getGoalAmount()) * 100);
        when(modelMapper.map(mockFund, FundInfo.class)).thenReturn(expectedFundInfo);
        FundInfo result = fundService.getFundInfoById(fundId);
        assertEquals(expectedFundInfo.getTitle(), result.getTitle());
        assertEquals(expectedFundInfo.getGoalAmount(), result.getGoalAmount());
        assertEquals(expectedFundInfo.getCurrentAmount(), result.getCurrentAmount());
        assertEquals(expectedFundInfo.getProgress(), result.getProgress());
        verify(fundRepository, times(1)).findById(fundId);
        verify(modelMapper, times(1)).map(mockFund, FundInfo.class);
    }

    @Test
    void testUpdateFund() {
        Long fundId = 1L;
        FundUpdateCommand command = new FundUpdateCommand();
        Fund mockFund = new Fund();
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(mockFund));
        FundUpdateInfo mockFundUpdateInfo = new FundUpdateInfo();
        when(modelMapper.map(mockFund, FundUpdateInfo.class)).thenReturn(mockFundUpdateInfo);
        FundUpdateInfo result = fundService.updateFund(fundId, command);
        assertNotNull(result);
        assertEquals(mockFundUpdateInfo, result);
        verify(fundRepository, times(1)).findById(fundId);
    }

    @Test
    void testUpdateFundWithImages() {
        Long fundId = 1L;
        FundUpdateCommand command = new FundUpdateCommand();

        // Example: Creating MockMultipartFile instances
        MockMultipartFile file1 = new MockMultipartFile("file", "filename1.txt", "text/plain", "some text".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "filename2.txt", "text/plain", "some other text".getBytes());

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(file1);
        multipartFiles.add(file2);
        command.setImages(multipartFiles);

        Fund mockFund = new Fund();
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(mockFund));
        FundUpdateInfo mockFundUpdateInfo = new FundUpdateInfo();
        when(modelMapper.map(mockFund, FundUpdateInfo.class)).thenReturn(mockFundUpdateInfo);
        FundUpdateInfo result = fundService.updateFund(fundId, command);
        assertNotNull(result);
        assertEquals(mockFundUpdateInfo, result);
        verify(fundRepository, times(1)).findById(fundId);
    }


}