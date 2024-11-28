package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {

    @Query("from PaymentInfo p where p.id =:id and p.shareGroup.id = :groupId")
    List<PaymentInfo> findAllByIdAndShareGroup(long id, long groupId);

    @Modifying
    @Query("delete from PaymentInfo p where p.bill.id in :billId")
    void deleteAllByBillIds(List<Long> billId);
}
