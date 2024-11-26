package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

//    Optional<AppUser> findById(long id);
}
