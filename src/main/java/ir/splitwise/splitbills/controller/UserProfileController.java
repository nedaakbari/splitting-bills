package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.BaseRequest;
import ir.splitwise.splitbills.models.DeptResponse;
import ir.splitwise.splitbills.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserProfileController {
    private final ExpenseService expenseService;

    @PostMapping("/expense")
    public DeptResponse getAllDeptOfUser(@RequestBody BaseRequest request) throws UserNotFoundException, ContentNotFoundException {
        expenseService.getALlDeptOfGroup(request.id());
        return expenseService.getAllExpenseOfUser(request.id());
    }
}
