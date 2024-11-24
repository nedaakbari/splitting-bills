package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.entity.State;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShareGroupService {
    private final ShareGroupRepository shareGroupRepository;
    private final UserService userService;

    @Transactional(rollbackOn = Throwable.class)
    public BaseRequest addShareGroup(ShareGroupRequest shareGroupRequest) throws UserNotFoundException {
        AppUser owner = userService.findUserById(1);//todo user owner

        List<Long> userIds = shareGroupRequest.userIds();
        List<AppUser> groupMembers = userService.findAllUserById(userIds);
        groupMembers.add(owner);

        ShareGroup shareGroup = buildGroup(shareGroupRequest, groupMembers, owner);
        ShareGroup savedGroupInDb = shareGroupRepository.save(shareGroup);
        //todo notify to all members

        for (AppUser groupMember : groupMembers) {
            List<Long> groupIds = groupMember.getGroupIds();
            groupIds.add(savedGroupInDb.getId());
        }

        userService.saveUsers(groupMembers);

        return new BaseRequest(savedGroupInDb.getId());
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

    public void modifyShareGroup(ModifySharedGroupRequest request) throws Exception {
        AppUser requester = userService.findUserById(1);//todo user owner
        var foundGroup = findGroupById(request.groupId());
        GroupMode groupMode = foundGroup.getGroupMode();
        if (GroupMode.OWNER_ONLY.equals(groupMode) && !Objects.equals(requester.getId(), foundGroup.getOwner().getId())) {
            throw new Exception("access deny");//todo better exception
        }

        foundGroup.setTitle(request.title());
        foundGroup.setDescription(request.description());
        List<Long> userIds = request.userIds();

        List<AppUser> members = userService.findAllUserById(userIds);
        members.add(requester);
        foundGroup.setMembers(members);

        shareGroupRepository.save(foundGroup);
    }

    public ShareGroup findGroupById(long id) throws ContentNotFoundException {
        return shareGroupRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("group " + id + "not found"));
    }

    public void deleteAGroup(long id) throws ContentNotFoundException {
        ShareGroup foundGroup = findGroupById(id);
//        foundGroup.setState(State.DELETE);
        shareGroupRepository.delete(foundGroup);
    }

    public ShareGroup saveGroupInDb(ShareGroup group) {
        return shareGroupRepository.save(group);
    }

    public List<ActiveShareGroupResponse> getAllActiveGroupOfAUser() throws UserNotFoundException {
        AppUser requester = userService.findUserById(1);//todo user owner
        List<Long> groupIds = requester.getGroupIds();
        List<ShareGroup> allGroupAfActiveUser = findAllGroupAfActiveUser(groupIds);

        return allGroupAfActiveUser.stream()
                .map(g -> new ActiveShareGroupResponse(g.getTitle(), g.getTotalCost(), g.getDescription())).toList();

    }

    public List<ShareGroupResponse> getAllGroupOfAUser() throws UserNotFoundException {
        AppUser requester = userService.findUserById(1);//todo user owner
        List<Long> groupIds = requester.getGroupIds();
        List<ShareGroup> allGroupAfUser = findAllGroupAfUser(groupIds);
        return allGroupAfUser.stream()
                .map(g -> new ShareGroupResponse(g.getTitle(), g.getTotalCost(), g.getDescription(), g.getState())).toList();
    }

    public List<ShareGroup> findAllGroupAfUser(List<Long> groupIds) {
        return shareGroupRepository.findAllById(groupIds);
    }

    private List<ShareGroup> findAllGroupAfActiveUser(List<Long> groupIds) {
        return shareGroupRepository.findAllByIdAndState(groupIds, State.ACTIVE);
    }
}
