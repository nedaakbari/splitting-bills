package ir.splitwise.splitbills.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

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
}
