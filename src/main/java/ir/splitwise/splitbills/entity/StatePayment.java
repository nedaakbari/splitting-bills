package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.PayWay;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class StatePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//todo AUTO=sequence is better or identity
    private Long id;
    @ManyToOne
    private User user;
    private PayWay payWay;
    private Date payDate;
    private boolean isPaid;
    private boolean isNotify;
}
