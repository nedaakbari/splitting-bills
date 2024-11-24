package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.entity.State;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.GroupMode;
import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

        ShareGroup shareGroup = buildGroup(shareGroupRequest, groupMembers, owner);
        ShareGroup savedGroupInDb = shareGroupRepository.save(shareGroup);
        //todo notify to all members
        return savedGroupInDb.getId();
    }

    private static ShareGroup buildGroup(ShareGroupRequest shareGroupRequest,
                                         List<AppUser> groupMembers, AppUser owner) {
        ShareGroup shareGroup = new ShareGroup();
        shareGroup.setMembers(groupMembers);
        shareGroup.setDescription(shareGroupRequest.description());
        shareGroup.setTitle(shareGroupRequest.title());
        shareGroup.setGroupMode(shareGroupRequest.justOwnerModify() ? GroupMode.OWNER_ONLY : GroupMode.COLLABORATIVE);
        shareGroup.setState(State.NEW);
        shareGroup.setOwner(owner);
        return shareGroup;
    }

    public void modifyShareGroup(ModifySharedGroupRequest request) throws Exception {
        AppUser requester = userService.findUserById(1);//todo user owner
        var foundGroup = findGroupById(request);
        GroupMode groupMode = foundGroup.getGroupMode();
        if (GroupMode.OWNER_ONLY.equals(groupMode) && !Objects.equals(requester.getId(), foundGroup.getOwner().getId())) {
            throw new Exception("access deny");//todo better exception
        }
    }

    private ShareGroup findGroupById(ModifySharedGroupRequest request) throws ContentNotFoundException {
        return shareGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new ContentNotFoundException("group " + request.groupId() + "not found"));
    }
}
