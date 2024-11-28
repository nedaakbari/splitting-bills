package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
import ir.splitwise.splitbills.models.enumeration.State;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShareGroupService {
    private final ShareGroupRepository shareGroupRepository;
    private final UserService userService;

    @Transactional(rollbackFor = Throwable.class)
    public BaseRequestResponse addShareGroup(ShareGroupRequest shareGroupRequest, AppUser owner) throws UserNotFoundException {

        List<Long> userIds = shareGroupRequest.userIds();
        List<AppUser> groupMembers = userService.findAllUserById(userIds);
        groupMembers.add(owner);
//todo what can i do that he can not add himself to member
        ShareGroup shareGroup = buildGroup(shareGroupRequest, groupMembers, owner);
        ShareGroup savedGroupInDb = shareGroupRepository.save(shareGroup);
        //todo notify to all members

/*        for (AppUser groupMember : groupMembers) {
            List<ShareGroup> shareGroupList = groupMember.getGroupIds();
            shareGroupList.add(savedGroupInDb);
        }

        userService.saveUsers(groupMembers);*/

        return new BaseRequestResponse(savedGroupInDb.getId());
    }

    private static ShareGroup buildGroup(ShareGroupRequest shareGroupRequest,
                                         List<AppUser> groupMembers, AppUser owner) {
        ShareGroup shareGroup = new ShareGroup();
        shareGroup.setMembers(groupMembers);
        shareGroup.setDescription(shareGroupRequest.description());
        shareGroup.setTitle(shareGroupRequest.title());
        shareGroup.setGroupMode(shareGroupRequest.justOwnerModify() ? GroupMode.OWNER_ONLY : GroupMode.COLLABORATIVE);
        shareGroup.setState(State.ACTIVE);
        shareGroup.setOwner(owner);
        return shareGroup;
    }

    public void modifyShareGroup(ModifySharedGroupRequest request, AppUser requester) throws Exception {
        var foundGroup = findGroupById(request.groupId());
        GroupMode groupMode = foundGroup.getGroupMode();
        if (GroupMode.OWNER_ONLY.equals(groupMode) && !Objects.equals(requester.getId(), foundGroup.getOwner().getId())) {
            throw new Exception("access deny");//todo better exception
        }

        foundGroup.setTitle(request.title());
        foundGroup.setDescription(request.description());
        Set<Long> userIds = request.userIds();
        List<AppUser> members = userService.findAllUserById(new ArrayList<>(userIds));
        foundGroup.setMembers(members);
        shareGroupRepository.save(foundGroup);
    }

    public ShareGroup findGroupById(long id) throws ContentNotFoundException {
        return shareGroupRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("group " + id + "not found"));
    }

    public void deleteAGroup(long id) throws ContentNotFoundException {
        ShareGroup foundGroup = findGroupById(id);
        shareGroupRepository.delete(foundGroup);
        //delete alla relative tables?
    }

    public ShareGroup saveGroupInDb(ShareGroup group) {
        return shareGroupRepository.save(group);
    }

    public List<ActiveShareGroupResponse> getAllActiveGroupOfAUser(AppUser requester) {
        List<ShareGroup> allGroupAfActiveUser = findAllGroupAfActiveUser(requester.getId());

        return allGroupAfActiveUser.stream()
                .map(g -> new ActiveShareGroupResponse(g.getTitle(), g.getTotalCost(), g.getDescription())).toList();

    }

    public List<ShareGroupResponse> getAllGroupOfAUser(AppUser requester) {
        List<ShareGroup> allGroupAfUser = findAllGroupAfUser(requester.getId());
        return allGroupAfUser.stream()
                .map(g -> new ShareGroupResponse(g.getTitle(), g.getTotalCost(), g.getDescription(), g.getState())).toList();
    }

    public List<ShareGroup> findAllGroupAfUser(long userId) {
        return shareGroupRepository.findAllGroupUser(userId);
    }

    private List<ShareGroup> findAllGroupAfActiveUser(long userId) {
        return shareGroupRepository.findAllActiveGroupUser(userId, State.ACTIVE);
    }
}
