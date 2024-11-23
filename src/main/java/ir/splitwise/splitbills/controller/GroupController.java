package ir.splitwise.splitbills.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    @PostMapping("/add")
    public void addGroup(){
        //todo
    }

    @PostMapping("/modify")
    public void modifyGroup(){//delete and modify possible fields
        //todo
    }




    @PostMapping("/ge")
    public void getAllGroupOfUser(){

    }


    @PostMapping("/")
    public void getAllActiveGroupOfUser(){

    }
}
