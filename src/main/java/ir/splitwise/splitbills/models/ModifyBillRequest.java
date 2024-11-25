package ir.splitwise.splitbills.models;

import java.util.List;

public record ModifyBillRequest(long id,
                                String title,
                                String description,
                                long payerId,
                                List<ItemRequest> items) {
}
