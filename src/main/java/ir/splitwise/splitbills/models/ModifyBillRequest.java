package ir.splitwise.splitbills.models;

public record ModifyBillRequest(long id,
                                String title,
                                String description,
                                long payer) {
}
