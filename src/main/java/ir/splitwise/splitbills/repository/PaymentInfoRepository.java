package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {

    @Query("from PaymentInfo p where p.payer.id =:userId and p.shareGroup.id = :groupId")
    List<PaymentInfo> findAllUserDeptPaymentInfo(long userId, long groupId);

    @Query("from PaymentInfo p where p.receiver.id =:userId and p.shareGroup.id = :groupId")
    List<PaymentInfo> findAllUserRecivePaymentInfo(long userId, long groupId);

    @Modifying
    @Query("delete from PaymentInfo p where p.shareGroup.id = :groupId")
    void deleteAllByGroupId(long groupId);
}
