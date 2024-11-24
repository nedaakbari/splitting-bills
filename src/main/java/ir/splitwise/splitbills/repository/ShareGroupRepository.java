package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.ShareGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {
}
