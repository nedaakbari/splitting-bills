package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("from Bill b where b.shareGroup.id = :groupId")
    List<Bill> findAllByGroupId(long groupId);
}
