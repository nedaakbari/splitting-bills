package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.GroupMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shareGroup")
    private List<Bill> billList;
    private double totalCost;//todo it must be autho calculate
    private String description;
    @Column(nullable = false)
    private GroupMode groupMode;

    //todo share picture of the group
    //todo List<Byte> pictures;
    private State state;
}
