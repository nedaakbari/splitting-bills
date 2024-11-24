package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.GroupMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Entity
@Getter
@Setter
public class ShareGroup extends BaseEntity {
    private String title;
    @ManyToOne
    private AppUser owner;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<AppUser> members;
    @OneToMany
    private List<Bill> billList;
    private double totalCost;
    private String description;
    @Column(nullable = false)
    private GroupMode groupMode;

    //todo share picture of the group
    //todo List<Byte> pictures;
    private State state;
}
