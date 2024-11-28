package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.enumeration.PayWay;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

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

    @ManyToOne
    private Bill bill;

    private PayWay payWay;
    private Date payDate;
    private boolean isPaid;
    private boolean isNotify;

//todo for transaction Info
//    private PayWay payWay;
//@Temporal(TemporalType.TIMESTAMP)
//@CreatedDate
//private Date creationDate;

}
