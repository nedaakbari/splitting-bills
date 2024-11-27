package ir.splitwise.splitbills.entity;

import ir.splitwise.splitbills.models.enumeration.PayWay;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class StatePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private AppUser appUser;
    private PayWay payWay;
    private Date payDate;
    private boolean isPaid;
    private boolean isNotify;
}
