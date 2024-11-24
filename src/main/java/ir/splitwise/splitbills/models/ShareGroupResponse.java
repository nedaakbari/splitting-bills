package ir.splitwise.splitbills.models;

import ir.splitwise.splitbills.entity.State;

public record ShareGroupResponse(String title,
                                 double totalCost,
                                 String description,
                                 State state) {
}
