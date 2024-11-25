package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Expense extends BaseEntity {
    @ManyToOne
    private AppUser appUser;
    @ManyToOne
    private Bill bill;

    private double shareAmount;
}
