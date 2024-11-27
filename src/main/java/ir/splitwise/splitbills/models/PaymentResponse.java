package ir.splitwise.splitbills.models;

import ir.splitwise.splitbills.models.enumeration.PayWay;

public record PaymentResponse(AppUserResponse payer,
                              AppUserResponse receiver
        , double amount) {
}
