package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.FlashSale;
import hu.progmasters.fundraiser.dto.incoming.FlashSaleItemSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleCreateInfo;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleItemInfo;
import hu.progmasters.fundraiser.exception.FlashSaleItemNotFoundException;
import hu.progmasters.fundraiser.repository.FlashSaleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FlashSaleService {

    private final FlashSaleRepository flashSaleRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;


    public FlashSaleCreateInfo saveFlashSaleItem(FlashSaleItemSaveUpdateCommand command) {
        FlashSale flashSale = modelMapper.map(command, FlashSale.class);
        flashSale.setGradeValue(command.getGrade().getValue());
        flashSaleRepository.save(flashSale);
        return modelMapper.map(flashSale, FlashSaleCreateInfo.class);
    }

    public List<FlashSaleItemInfo> getFlashSaleItemByAccountIdAndGrade(Long accountId) {
        Account account = accountService.findAccountById(accountId);
        List<FlashSale> flashSaleItems = flashSaleRepository.findAllByGrade(account.getGrade().getValue());
        return flashSaleItems.stream()
                .map(flashSale -> {
                    int price = calculatePrice(flashSale.getGrade().getValue(), flashSale.getPriceInCoin(),
                            account.getGrade().getValue(), account.getGrade().getDiscount());
                    FlashSaleItemInfo flashSaleItemInfo = modelMapper.map(flashSale, FlashSaleItemInfo.class);
                    flashSaleItemInfo.setPriceInCoin(price);
                    return flashSaleItemInfo;
                })
                .collect(Collectors.toList());
    }

    public int calculatePrice(int flashSaleValue, int coin, int accountValue, int accountDiscount) {
        return flashSaleValue < accountValue ? coin * (100 - accountDiscount) / 100 : coin;
    }

    public FlashSale findItemById(Long itemId) {
        return flashSaleRepository.findById(itemId).orElseThrow(() -> new FlashSaleItemNotFoundException(itemId));
    }


//    public int calculateAndSetPrice(int priceInCoin, Grade grade) {
//        return (int) (priceInCoin * (1 - grade.getDiscount() / 100.0));
//    }

}
