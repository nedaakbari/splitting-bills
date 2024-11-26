package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.ActiveShareGroupResponse;
import ir.splitwise.splitbills.models.BaseRequest;
import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.models.ShareGroupResponse;
import ir.splitwise.splitbills.service.ModifySharedGroupRequest;
import ir.splitwise.splitbills.service.ShareGroupService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name= "Share Group APIs",
description = "This category includes APIs for groups who want to share expenses. ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final ShareGroupService shareGroupService;

    @PostMapping("/add")
    public BaseRequest addGroup(@RequestBody ShareGroupRequest request)
            throws UserNotFoundException {

        return shareGroupService.addShareGroup(request);
    }

    @PostMapping("/modify")
    public void modifyGroup(@RequestBody ModifySharedGroupRequest request)
            throws Exception {//delete and modify possible fields

        shareGroupService.modifyShareGroup(request);
    }

    @PostMapping("/delete")
    public void deleteGroup(@RequestBody BaseRequest request)
            throws ContentNotFoundException {

        shareGroupService.deleteAGroup(request.id());
    }

    @GetMapping("/get-all")
    public List<ShareGroupResponse> getAllGroupOfUser() throws UserNotFoundException {
        return shareGroupService.getAllGroupOfAUser();
    }

    @GetMapping("/get-all-active")
    public List<ActiveShareGroupResponse> getAllActiveGroupOfUser() throws UserNotFoundException {
        return shareGroupService.getAllActiveGroupOfAUser();
    }

    @GetMapping("/get-all-in-progress")
    public List<ActiveShareGroupResponse> getAllInProgressGroupOfUser() throws UserNotFoundException {
        return shareGroupService.getAllActiveGroupOfAUser();
    }
}
