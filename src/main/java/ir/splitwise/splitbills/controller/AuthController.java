package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.LoginRequest;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void register(@Validated @RequestBody RegisterUserRequest request, HttpServletResponse response)//HttpServletResponse response
            throws DuplicateDataException {

        authenticationService.register(request, response);
    }

    @PostMapping("/login")
    public void login(@Validated @RequestBody LoginRequest request, HttpServletResponse httpServletResponse) {
        authenticationService.login(request, httpServletResponse);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();//todo it is true?
        authenticationService.logout(response);
    }
}
