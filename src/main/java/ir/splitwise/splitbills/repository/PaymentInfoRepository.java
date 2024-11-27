package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
}
