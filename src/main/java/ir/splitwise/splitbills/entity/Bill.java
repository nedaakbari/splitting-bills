package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double totalCost;
    @ManyToOne
    private User payer;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private Date modyfyingDate;

}
