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

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ShareGroupService shareGroupService;

    public void addExpense(Bill bill, List<ItemRequest> itemRequestList) throws UserNotFoundException {
        List<Expense> expenseList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            double totalCost = itemRequest.getTotalCost();
            int itemTotalCount = itemRequest.getCount();
            List<UserItem> userItems = itemRequest.getUserItems();
            boolean equalShare = itemRequest.isEqualShare();
            if (equalShare) {
                expenseList.addAll(getEqualExpense(bill, totalCost, userItems));
            } else {
                for (UserItem userItem : userItems) {
                    expenseList.add(getPairExpense(bill, userItem, totalCost, itemTotalCount));
                }
            }
        }
        expenseRepository.saveAll(expenseList);
    }

    private Expense getPairExpense(Bill bill, UserItem userItem, double totalCost, int itemTotalCount) throws UserNotFoundException {
        int count = userItem.getCount();
        long userId = userItem.getUserId();
        AppUser userById = userService.findUserById(userId);
        Expense expense = new Expense();
        expense.setAppUser(userById);
        expense.setShareAmount((totalCost / itemTotalCount) * count);
        expense.setBill(bill);
        return expense;
    }

    private List<Expense> getEqualExpense(Bill bill, double totalCost, List<UserItem> userItems)
            throws UserNotFoundException {

        double sharedCount = totalCost / userItems.size();
        List<Long> list = userItems.stream().map(UserItem::getUserId).toList();//todo set?
        List<AppUser> allUserById = userService.findAllUserById(list);
        return allUserById.stream().map(user -> new Expense(user, bill, sharedCount)).toList();
    }

    public DeptResponse getAllExpenseOfUser(long groupId, AppUser requester) throws ContentNotFoundException {
        ShareGroup userGroup = shareGroupService.findGroupById(groupId);
        double totalDept = 0;
        List<Bill> billList = userGroup.getBillList();//todo lazy for  items
        for (Bill bill : billList) {
            List<Expense> expenses = expenseRepository.finaAllByBillIdAndUserId(bill.getId(), requester.getId());
            AppUser payer = bill.getPayer();
            if (Objects.equals(payer.getId(), requester.getId())) {
                totalDept -= bill.getTotalCost();
            }
            if (!expenses.isEmpty()) {
                totalDept += expenses.stream().mapToDouble(Expense::getShareAmount).sum();
            }
        }
        return new DeptResponse(totalDept);
    }

    public List<?> getALlDeptOfGroup(long groupId, AppUser requester) throws ContentNotFoundException {
        ShareGroup group = shareGroupService.findGroupById(groupId);
        double totalCost = group.getTotalCost();//todo validate

        Map<AppUser, Double> deptOfGroup = new HashMap<>();
        List<Bill> billList = group.getBillList();
        for (Bill bill : billList) {
            List<Expense> expenses = expenseRepository.finaAllByBillId(bill.getId());
            AppUser payer = bill.getPayer();
            Double payerCost = deptOfGroup.get(payer);
            double payerDept = payerCost == null ? 0 : payerCost;
            payerDept -= bill.getTotalCost();
            deptOfGroup.put(payer, payerDept);

            for (Expense expens : expenses) {
                AppUser appUser = expens.getAppUser();
                Double cost = deptOfGroup.get(appUser);
                double userDept = cost == null ? 0 : cost;
                userDept += expens.getShareAmount();
                deptOfGroup.put(appUser, userDept);
            }
        }
        return null;
    }
}
