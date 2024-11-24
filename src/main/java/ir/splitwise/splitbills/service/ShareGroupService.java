package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareGroupService {
    private final ShareGroupRepository shareGroupRepository;

    public void addShareGroup(ShareGroupRequest shareGroupRequest) {
        //todo
    }
}
