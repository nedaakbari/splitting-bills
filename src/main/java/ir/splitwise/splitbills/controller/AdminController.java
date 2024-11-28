package ir.splitwise.splitbills.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin APIs")
@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AuthenticationService authenticationService;

    @PostMapping("/add")
    @Operation(summary = "addAdmin", description = "this api used for adding a new admin to application just with another admin")
    public void addAmin(RegisterUserRequest request) throws DuplicateDataException {
        authenticationService.registerAdmin(request);
    }
}
