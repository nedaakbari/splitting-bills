package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.service.BaseRequest;
import ir.splitwise.splitbills.service.ModifySharedGroupRequest;
import ir.splitwise.splitbills.service.ShareGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void deleteGroup(@RequestBody BaseRequest request) {

        shareGroupService.deleteAGroup(request.id());
    }


    @PostMapping("/get-all")
    public void getAllGroupOfUser() {

    }


    @PostMapping("/")
    public void getAllActiveGroupOfUser() {

    }
}
