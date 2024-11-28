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

    @Column(columnDefinition = "TEXT")
    private String items;

    @ManyToOne
    private AppUser creator;

    @ManyToOne
    private AppUser modifier;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShareGroup shareGroup;


    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenseList;//todo problem fetch eager dont know why
}
