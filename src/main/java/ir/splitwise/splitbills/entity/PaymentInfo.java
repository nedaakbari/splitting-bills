package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private AppUser payer;
    @ManyToOne
    private AppUser receiver;
    private double amount;
    @ManyToOne
    private ShareGroup shareGroup;

    //todo for transaction Info
//    private PayWay payWay;
//@Temporal(TemporalType.TIMESTAMP)
//@CreatedDate
//private Date creationDate;
}
