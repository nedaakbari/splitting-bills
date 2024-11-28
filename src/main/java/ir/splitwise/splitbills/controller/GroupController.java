package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.CheckAppUser;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
import ir.splitwise.splitbills.service.ShareGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Share Group APIs", description = "This category includes APIs for groups who want to share expenses. ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final ShareGroupService shareGroupService;

    @PostMapping("/add")
    @Operation(description = "this api used for build a new group,it has a justOwnerModify that if be true just the creator can add or modify the group and it's bills")
    public BaseRequestResponse addGroup(@RequestBody ShareGroupRequest request, Authentication authentication)
            throws UserNotFoundException {

        AppUser appUser = CheckAppUser.checkUserInstance(authentication);
        return shareGroupService.addShareGroup(request, appUser);
    }

    @PostMapping("/modify")
    @Operation(description = "this api used for modify a group that already added ")
    public void modifyGroup(@RequestBody ModifySharedGroupRequest request, Authentication authentication)
            throws Exception {

        AppUser appUser = CheckAppUser.checkUserInstance(authentication);
        shareGroupService.modifyShareGroup(request, appUser);
    }

    @Operation(description = "this api used for delete a group that already added")
    @PostMapping("/delete")
    public void deleteGroup(@RequestBody BaseRequestResponse request)
            throws ContentNotFoundException {

        shareGroupService.deleteAGroup(request.id());
    }


    @Operation(description = "this api shows all groups the user is member of")
    @GetMapping("/get-all")
    public List<ShareGroupResponse> getAllGroupOfUser(Authentication authentication)
            throws UserNotFoundException {

        var appUser = CheckAppUser.checkUserInstance(authentication);
        return shareGroupService.getAllGroupOfAUser(appUser);
    }

    @GetMapping("/get-all-active")
    public List<ActiveShareGroupResponse> getAllActiveGroupOfUser(Authentication authentication)
            throws UserNotFoundException {

        var appUser = CheckAppUser.checkUserInstance(authentication);
        return shareGroupService.getAllActiveGroupOfAUser(appUser);
    }
}
