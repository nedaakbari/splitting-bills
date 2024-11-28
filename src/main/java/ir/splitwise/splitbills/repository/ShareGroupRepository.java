package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.models.enumeration.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {

    @Query("from ShareGroup s join fetch s.members m where m.id=:userId")
    List<ShareGroup> findAllGroupUser(long userId);

    @Query("from ShareGroup s join fetch AppUser a where a.id=:userId and s.state =:state")
    List<ShareGroup> findAllActiveGroupUser(long userId, State state);


}
