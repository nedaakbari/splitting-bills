package ir.splitwise.splitbills.models;

public record BillResponse(String title,
                           String description,
                           double totalCost,
                           AppUserResponse payer,
                           String items,
                           AppUserResponse creator,
                           AppUserResponse modifier) {
}
