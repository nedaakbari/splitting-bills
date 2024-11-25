package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

//    Optional<AppUser> findById(long id);
}
