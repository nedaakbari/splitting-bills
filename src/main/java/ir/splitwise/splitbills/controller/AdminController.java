package ir.splitwise.splitbills.controller;


import ir.splitwise.splitbills.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private UserService userService;


    @PostMapping("/add")
//    @PreAuthorize("hasRole('ADMIN')")
    public void addAmin(){
        //register an admin
    }
}
