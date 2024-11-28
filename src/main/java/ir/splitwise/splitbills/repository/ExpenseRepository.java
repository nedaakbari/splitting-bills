package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("from  Expense  e where e.bill.id =:billId and e.appUser.id = :userId")
    List<Expense> finaAllByBillIdAndUserId(long billId, long userId);

    @Query("from  Expense  e where e.bill.id =:billId ")
    List<Expense> finaAllByBillId(long billId);

    void deleteAllByBillId(long billId);

    @Modifying
    @Query("delete from Expense e where e.bill.id in :ids")
    void deleteAllByBillIs(List<Long> ids);
}
