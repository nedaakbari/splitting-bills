package ir.splitwise.splitbills.repository;

import ir.splitwise.splitbills.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
}
