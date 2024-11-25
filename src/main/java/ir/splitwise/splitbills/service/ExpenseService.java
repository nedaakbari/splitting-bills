package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.Expense;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.DeptResponse;
import ir.splitwise.splitbills.models.ItemRequest;
import ir.splitwise.splitbills.models.UserItem;
import ir.splitwise.splitbills.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ShareGroupService shareGroupService;

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

    public DeptResponse getAllExpenseOfUser(long groupId) throws UserNotFoundException, ContentNotFoundException {
        var userRequester = userService.findUserById(1);//todo get from spring
        ShareGroup userGroup = shareGroupService.findGroupById(groupId);
        double totalDept = 0;
        List<Bill> billList = userGroup.getBillList();//todo lazy for  items
        for (Bill bill : billList) {
            List<Expense> expenses = expenseRepository.finaAllByBillIdAndUserId(bill.getId(), userRequester.getId());
            AppUser payer = bill.getPayer();
            if (Objects.equals(payer.getId(), userRequester.getId())) {
                totalDept -= bill.getTotalCost();
            }
            if (!expenses.isEmpty()) {
                totalDept += expenses.stream().mapToDouble(Expense::getShareAmount).sum();
            }
        }
        return new DeptResponse(totalDept);
    }
}
