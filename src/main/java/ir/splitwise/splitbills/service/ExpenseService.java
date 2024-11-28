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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;


    public List<Expense> addExpense(Bill bill, List<ItemRequest> itemRequestList) throws UserNotFoundException {
        expenseRepository.deleteAllByBillId(bill.getId());
        List<Expense> expenseList = new ArrayList<>();
        for (var itemRequest : itemRequestList) {
            var totalCost = itemRequest.getTotalCost();
            var itemTotalCount = itemRequest.getCount();
            var userItems = itemRequest.getUserItems();
            var equalShare = itemRequest.isEqualShare();
            if (equalShare) {
                expenseList.addAll(getEqualExpense(bill, totalCost, userItems));
            } else {
                for (UserItem userItem : userItems) {
                    expenseList.add(getPairExpense(bill, userItem, totalCost, itemTotalCount));
                }
            }
        }
        expenseList.add(new Expense(bill.getPayer(), bill, bill.getTotalCost()));
        return expenseRepository.saveAll(expenseList);
    }

    private List<Expense> getEqualExpense(Bill bill, double totalCost, List<UserItem> userItems)
            throws UserNotFoundException {

        double sharedCount = totalCost / userItems.size();
        List<Long> list = userItems.stream().map(UserItem::getUserId).toList();//todo set?
        List<AppUser> allUserById = userService.findAllUserById(list);
        return allUserById.stream().map(user -> new Expense(user, bill, -sharedCount)).toList();
    }


    private Expense getPairExpense(Bill bill, UserItem userItem,
                                   double totalCost, int itemTotalCount) throws UserNotFoundException {
        int count = userItem.getCount();
        long userId = userItem.getUserId();
        AppUser userById = userService.findUserById(userId);
        Expense expense = new Expense();
        expense.setAppUser(userById);
        expense.setShareAmount(-(totalCost / itemTotalCount) * count);
        expense.setBill(bill);
        return expense;
    }
}
