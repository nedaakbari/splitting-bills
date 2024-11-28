package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.enumeration.GroupMode;
import ir.splitwise.splitbills.models.enumeration.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareGroup extends BaseEntity {

    @ManyToOne(cascade = CascadeType.REMOVE)
    private AppUser owner;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<AppUser> members;

    @Column(nullable = false)
    private GroupMode groupMode;

    private String title;
    private double totalCost;
    private String description;
    private State state;

    @OneToMany(mappedBy = "shareGroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    List<Bill> billList;

    @OneToMany(mappedBy = "shareGroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    List<PaymentInfo> paymentInfoList;

    @Builder
    public ShareGroup(AppUser owner,
                      List<AppUser> members,
                      GroupMode groupMode,
                      String title, String description) {
        this.owner = owner;
        this.members = members;
        this.groupMode = groupMode;
        this.title = title;
        this.description = description;
    }
}
