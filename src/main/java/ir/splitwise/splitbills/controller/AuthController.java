package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.AppUser;
import ir.splitwise.splitbills.models.AuthResponse;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public AuthResponse register(@Validated(value = AppUser.class) @RequestBody RegisterUserRequest request) throws DuplicateDataException {
        return authenticationService.register(request);
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
