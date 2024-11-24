package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.models.ShareGroupRequest;
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
    public void addGroup(@RequestBody ShareGroupRequest shareGroupRequest) {
        shareGroupService.addShareGroup(shareGroupRequest);
    }

    @PostMapping("/modify")
    public void modifyGroup() {//delete and modify possible fields
        //todo
    }


    @PostMapping("/ge")
    public void getAllGroupOfUser() {

    }


    @PostMapping("/")
    public void getAllActiveGroupOfUser() {

    }
}
