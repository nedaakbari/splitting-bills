package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Bill extends BaseEntity {

    private String title;
    private String description;
    private double totalCost;
    @ManyToOne
    private AppUser payer;
    @OneToMany
    private List<PairItem> items;//todo pairClass?

}
