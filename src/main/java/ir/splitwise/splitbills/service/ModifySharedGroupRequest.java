package ir.splitwise.splitbills.service;

import java.util.Set;

public record ModifySharedGroupRequest(long groupId,
                                       String title,
                                       Set<Long> userIds,
                                       String description) {
}
