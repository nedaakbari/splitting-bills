package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.GroupMode;
import ir.splitwise.splitbills.models.enumeration.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ShareGroup extends BaseEntity {

    @ManyToOne
    private AppUser owner;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<AppUser> members;

    @Column(nullable = false)
    private GroupMode groupMode;

    private String title;
    private double totalCost;
    private String description;
    private State state;
}
