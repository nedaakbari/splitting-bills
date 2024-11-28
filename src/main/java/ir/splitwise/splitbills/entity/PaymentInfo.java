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

    private PayWay payWay;
    private Date payDate;
    private boolean isPaid;
    private boolean isNotify;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentInfo that)) return false;
        return Objects.equals(getPayer(), that.getPayer()) && Objects.equals(getReceiver(), that.getReceiver()) && Objects.equals(getShareGroup(), that.getShareGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayer(), getReceiver(), getShareGroup());
    }
}
