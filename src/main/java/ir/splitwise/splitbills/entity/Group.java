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
public class Group extends BaseEntity {
    private String title;
    @ManyToMany
    private List<User> members;
    private double totalCost;
    private String description;

    @OneToMany
    private List<Bill> billList;

    //todo share picture of the group
}
