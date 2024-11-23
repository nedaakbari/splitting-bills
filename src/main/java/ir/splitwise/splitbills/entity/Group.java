package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToMany
    private List<User> members;
    private Date creationDate;
    private Date modyfyingDate;
    private double totalCost;
    private String description;

    @OneToMany
    private List<Bill> billList;

    //todo share picture of the group

}
