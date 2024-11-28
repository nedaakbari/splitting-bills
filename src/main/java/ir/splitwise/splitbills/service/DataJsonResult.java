package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.models.PaymentResponse;

import java.util.List;

public record DataJsonResult(List<PaymentResponse> jsonResult) {
}
