package ir.splitwise.splitbills.models;

import ir.splitwise.splitbills.entity.State;

public record ActiveShareGroupResponse(String title,
                                       double totalCost,
                                       String description) {
}
