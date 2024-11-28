package ir.splitwise.splitbills.models;

import java.util.List;

public record AddBillRequest(long groupId,
                             String title,
                             String description,
                             double totalCost,
                             long payerId,
                             List<ItemRequest> items) {
}
