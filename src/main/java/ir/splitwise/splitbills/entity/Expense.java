package ir.splitwise.splitbills.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends BaseEntity {
    @ManyToOne
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.ALL)
    private Bill bill;

    private double shareAmount;
}
