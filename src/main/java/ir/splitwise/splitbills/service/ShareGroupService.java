package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.entity.State;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.GroupMode;
import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareGroupService {
    private final ShareGroupRepository shareGroupRepository;
    private final UserService userService;

    public long addShareGroup(ShareGroupRequest shareGroupRequest) throws UserNotFoundException {
        AppUser owner = userService.findUserById(1);//todo user owner

        List<Long> userIds = shareGroupRequest.userIds();
        List<AppUser> groupMembers = userService.findAllUserById(userIds);
        groupMembers.add(owner);

        ShareGroup shareGroup = buildGroup(shareGroupRequest, groupMembers);
        ShareGroup savedGroupInDb = shareGroupRepository.save(shareGroup);
        //todo notify to all members
        return savedGroupInDb.getId();
    }

    private static ShareGroup buildGroup(ShareGroupRequest shareGroupRequest, List<AppUser> groupMembers) {
        ShareGroup shareGroup = new ShareGroup();
        shareGroup.setMembers(groupMembers);
        shareGroup.setDescription(shareGroupRequest.description());
        shareGroup.setTitle(shareGroupRequest.title());
        shareGroup.setGroupMode(shareGroupRequest.justOwnerModify() ? GroupMode.OWNER_ONLY : GroupMode.COLLABORATIVE);
        shareGroup.setState(State.NEW);
        return shareGroup;
    }
}
