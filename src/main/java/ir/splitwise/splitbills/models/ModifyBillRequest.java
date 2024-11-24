package ir.splitwise.splitbills.models;

public record ModifyBillRequest(String title,
                                String description,
                                long payer,
                                String items) {
}
