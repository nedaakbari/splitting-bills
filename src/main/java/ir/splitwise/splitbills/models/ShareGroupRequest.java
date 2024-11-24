package ir.splitwise.splitbills.models;

import java.util.List;

public record ShareGroupRequest(String title,
                                List<Long> userIds,
                                String description,
                                boolean justOwnerModify) {
}
