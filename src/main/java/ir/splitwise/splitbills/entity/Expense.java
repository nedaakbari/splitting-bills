package ir.splitwise.splitbills.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Expense extends BaseEntity {
    @ManyToOne
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.ALL)
    private Bill bill;

    private double shareAmount;
    ;


    public Expense(AppUser appUser, Bill bill, double shareAmount) {
        this.appUser = appUser;
        this.bill = bill;
        this.shareAmount = shareAmount;
    }
}
