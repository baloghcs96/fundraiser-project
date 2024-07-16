package hu.progmasters.fundraiser.repository;

import hu.progmasters.fundraiser.domain.entity.Transaction;
import hu.progmasters.fundraiser.domain.enumeration.TransactionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t from Transaction t WHERE t.targetFund.fundId = :fundId")
    List<Transaction> findAllByFundId(@Param("fundId") Long fundId);

    @Query("SELECT t from Transaction t WHERE t.senderAccount.accountId = :accountId")
    List<Transaction> findAllBySenderAccountId(Long accountId);

    @Query("SELECT t from Transaction t WHERE t.transactionState = :state")
    List<Transaction> findAllByTransactionState(TransactionState state);
}
