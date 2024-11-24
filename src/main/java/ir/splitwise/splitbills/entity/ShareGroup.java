package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ShareGroup extends BaseEntity {
    private String title;
    @ManyToMany
    private List<AppUser> members;
    @OneToMany
    private List<Bill> billList;
    private double totalCost;
    private String description;


    //todo share picture of the group
    //todo List<Byte> pictures;
    private State state;
}
