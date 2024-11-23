package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
