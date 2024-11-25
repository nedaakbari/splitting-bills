package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.Expense;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.ItemRequest;
import ir.splitwise.splitbills.models.UserItem;
import ir.splitwise.splitbills.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public void addExpense(Bill bill, List<ItemRequest> itemRequestList) throws UserNotFoundException {
        for (ItemRequest itemRequest : itemRequestList) {
            double totalCost = itemRequest.getTotalCost();
            int itemTotalCount = itemRequest.getCount();
            List<UserItem> userItems = itemRequest.getUserItems();
            for (UserItem userItem : userItems) {
                int count = userItem.getCount();
                long userId = userItem.getUserId();
                AppUser userById = userService.findUserById(userId);
                Expense expense = new Expense();
                expense.setAppUser(userById);
                expense.setShareAmount((totalCost / itemTotalCount) * count);
                expense.setBill(bill);
                expenseRepository.save(expense);
            }
        }
    }
}
