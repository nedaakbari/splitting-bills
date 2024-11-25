package ir.splitwise.splitbills.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class Bill extends BaseEntity {

    private String title;
    private String description;
    private double totalCost;
    @ManyToOne
    private AppUser payer;
//    @Lob//todo how many character?
    @Column(columnDefinition = "TEXT")
    private String items;//todo pairClass?

    @ManyToOne//todo can not modify
    private AppUser creator;
    @ManyToOne
    private AppUser modifier;

    @ManyToOne
    @ToString.Exclude
    private ShareGroup shareGroup;
}
