package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PairItem extends BaseEntity {
    private String itemName;
    private int count;
    private double cost;
/*    @ManyToOne
    private AppUser appUser;*/
}
