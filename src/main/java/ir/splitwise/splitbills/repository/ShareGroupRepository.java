package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {

    @Query("from ShareGroup s where s.id in :id and s.state = :state")
    List<ShareGroup> findAllByIdAndState(List<Long> id, State state);

    @Query("from ShareGroup s join fetch AppUser a where a.id=:userId")
    List<ShareGroup> findAllGroupUser(long userId);

    @Query("from ShareGroup s join fetch AppUser a where a.id=:userId and s.state =:state")
    List<ShareGroup> findAllActiveGroupUser(long userId, State state);
}
