package ir.splitwise.splitbills.models;

import ir.splitwise.splitbills.models.enumeration.State;

public record ShareGroupResponse(String title,
                                 double totalCost,
                                 String description,
                                 State state) {
}
