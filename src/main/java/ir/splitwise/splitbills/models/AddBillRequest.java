package ir.splitwise.splitbills.models;

import com.google.gson.JsonElement;

import java.util.List;

public record AddBillRequest(long groupId,
                             String title,
                             String description,
                             double totalCost,
                             long payerId,
                             List<ItemRequest> items) {//todo what can i do for this
}
