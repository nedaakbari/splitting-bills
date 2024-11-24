package ir.splitwise.splitbills.entity;

import jakarta.persistence.ManyToOne;

public class Expense extends BaseEntity {
    @ManyToOne
    private SahreGroup sahreGroup;
    //todo
}
