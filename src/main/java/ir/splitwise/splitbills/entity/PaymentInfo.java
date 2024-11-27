package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser payer;
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser receiver;
    private double amount;
    @ManyToOne(fetch = FetchType.LAZY)
    private ShareGroup shareGroup;

    //todo for transaction Info
//    private PayWay payWay;
//@Temporal(TemporalType.TIMESTAMP)
//@CreatedDate
//private Date creationDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentInfo that)) return false;
        return Double.compare(getAmount(), that.getAmount()) == 0 &&
                Objects.equals(getPayer(), that.getPayer()) &&
                Objects.equals(getReceiver(), that.getReceiver())
                && Objects.equals(getShareGroup(), that.getShareGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayer(), getReceiver(), getAmount(), getShareGroup());
    }
}
