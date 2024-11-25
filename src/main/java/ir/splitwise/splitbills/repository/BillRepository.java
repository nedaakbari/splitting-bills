package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
