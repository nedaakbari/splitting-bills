package ir.splitwise.splitbills.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    //todo authenticate


    @PostMapping("/register")
    public void register(){
        //todo
    }

    @PostMapping("/login")
    public void login(){
        //todo
    }

    @PostMapping("/logout")
    public void logout(){
        //todo
    }
}
