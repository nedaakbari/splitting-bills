package ir.splitwise.splitbills.models;

public record PaymentResponse(AppUserResponse payer,
                              AppUserResponse receiver,
                              double amount) {
}
