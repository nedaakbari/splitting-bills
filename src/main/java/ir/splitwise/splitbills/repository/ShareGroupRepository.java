package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.entity.State;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {

    @Query("from ShareGroup s where s.id in :id and s.state = :state")
    List<ShareGroup> findAllByIdAndState(List<Long> id, State state);

    List<ShareGroup> findAllBy();

    List<ShareGroup> findAllActiveGroupOfUser();
}
