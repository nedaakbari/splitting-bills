package ir.splitwise.splitbills.service;

import java.util.List;

public record ModifySharedGroupRequest(long groupId,
                                       String title,
                                       List<Long> userIds,
                                       String description) {
}
