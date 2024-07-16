package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.FlashSale;
import hu.progmasters.fundraiser.domain.entity.Purchase;
import hu.progmasters.fundraiser.dto.incoming.PurchaseCreateCommand;
import hu.progmasters.fundraiser.dto.outgoing.PurchaseInfo;
import hu.progmasters.fundraiser.exception.NotEnoughCoinToPurchaseException;
import hu.progmasters.fundraiser.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final AccountService accountService;
    private final FlashSaleService flashSaleService;
    private final ModelMapper modelMapper;

    private static void setPurchaseDetails(int quantity, Purchase purchase, String item, int price) {
        purchase.setItem(item);
        purchase.setPriceInCoin(price * quantity);
        purchase.setPurchaseTime(LocalDateTime.now());
        purchase.setQuantity(quantity);
    }

    public void savePurchase(PurchaseCreateCommand command, Long accountId) {
        Purchase purchase = modelMapper.map(command, Purchase.class);
        Account account = accountService.findAccountById(accountId);
        FlashSale item = flashSaleService.findItemById(command.getItemId());
        int price = flashSaleService.calculatePrice(item.getGrade().getValue(), item.getPriceInCoin(),
                account.getGrade().getValue(), account.getGrade().getDiscount());
        enoughCoinCheck(account.getCoin(), price);
        account.setCoin(account.getCoin() - (price * command.getQuantity()));
        purchase.setAccount(account);
        setPurchaseDetails(command.getQuantity(), purchase, item.getItem(), price);
        item.setQuantity(item.getQuantity() - command.getQuantity());
        purchaseRepository.save(purchase);
    }

    public List<PurchaseInfo> getPurchase(Long accountId) {
        Account account = accountService.findAccountById(accountId);
        List<Purchase> purchases = purchaseRepository.findPurchasesByAccountId(account.getAccountId());
        return purchases.stream()
                .map(purchase -> modelMapper.map(purchase, PurchaseInfo.class))
                .collect(Collectors.toList());

    }

    private void enoughCoinCheck(int accountCoin, int price) {
        if (accountCoin < price) {
            throw new NotEnoughCoinToPurchaseException(accountCoin);
        }
    }

}
