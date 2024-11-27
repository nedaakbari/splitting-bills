package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.enumeration.PayWay;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

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

    private PayWay payWay;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date creationDate;

    @ManyToOne
    private Bill billList;
}
