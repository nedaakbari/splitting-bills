package ir.splitwise.splitbills.controller;


import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AuthenticationService authenticationService;

    @PostMapping("/add")
//    @PreAuthorize("hasRole('ADMIN')")
    public void addAmin(RegisterUserRequest request) throws DuplicateDataException {
        authenticationService.registerAdmin(request);
    }
}
