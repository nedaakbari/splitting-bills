package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name= "Authentication APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    //todo authenticate


    @PostMapping("/register")
    public void register() {
        //todo
    }

    @PostMapping("/login")
    public void login() {
        //todo
    }

    @PostMapping("/logout")
    public void logout() {
        //todo
    }
}
