package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.splitwise.splitbills.entity.*;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AppUserResponse;
import ir.splitwise.splitbills.models.ItemRequest;
import ir.splitwise.splitbills.models.PaymentResponse;
import ir.splitwise.splitbills.models.UserItem;
import ir.splitwise.splitbills.repository.BillRepository;
import ir.splitwise.splitbills.repository.ExpenseRepository;
import ir.splitwise.splitbills.repository.PaymentInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ShareGroupService shareGroupService;
    private final UserService userService;
    private final BillRepository billRepository;
    private final Gson gson;
    private static final Type itemList = new TypeToken<List<ItemRequest>>() {
    }.getType();

    @Transactional(rollbackFor = Throwable.class)
    public List<PaymentResponse> getPayInfoOfGroup(long groupId) throws ContentNotFoundException, UserNotFoundException {
        ShareGroup foundGroup = shareGroupService.findGroupById(groupId);
        List<Bill> billListOfAGroup = billRepository.findAllByGroupId(groupId);

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        for (Bill bill : billListOfAGroup) {
            var list = getExpenseDtoList(bill);
            var paymentInfo = processPayInfo(list, foundGroup, bill);
            paymentInfoList.addAll(paymentInfo);
        }
        paymentInfoRepository.deleteAllByBillIds(billListOfAGroup.stream().map(Bill::getId).toList());
        var savedPayment = paymentInfoRepository.saveAll(paymentInfoList);
        return savedPayment.stream().map(x -> new PaymentResponse(new AppUserResponse(x.getPayer().getUsername()),
                new AppUserResponse(x.getReceiver().getUsername()), x.getAmount())).toList();

    }

    private List<ExpenseDto> getExpenseDtoList(Bill bill) throws UserNotFoundException {
        List<ItemRequest> itemRequest = gson.fromJson(bill.getItems(), itemList);
        var expenses = addExpense(bill, itemRequest);
        return expenses.stream()
                .map(expense -> new ExpenseDto(expense.getAppUser(), expense.getBill(), expense.getShareAmount()))
                .toList();
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

    private List<Expense> getEqualExpense(Bill bill, double totalCost, List<UserItem> userItems)
            throws UserNotFoundException {

        double sharedCount = totalCost / userItems.size();
        List<Long> list = userItems.stream().map(UserItem::getUserId).toList();//todo set?
        List<AppUser> allUserById = userService.findAllUserById(list);
        return allUserById.stream().map(user -> new Expense(user, bill, -sharedCount)).toList();
    }

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

    @Transactional(readOnly = true)
    List<PaymentInfo> processPayInfo(List<ExpenseDto> expenses, ShareGroup shareGroup, Bill bill) {
        List<ExpenseDto> payer = new ArrayList<>();
        List<ExpenseDto> recivers = new ArrayList<>();
        for (ExpenseDto expens : expenses) {
            if (expens.getShareAmount() < 0) {
                payer.add(expens);
            } else {
                recivers.add(expens);
            }
        }

        payer.sort(Comparator.comparingDouble(ExpenseDto::getShareAmount));
        recivers.sort(Comparator.comparingDouble(ExpenseDto::getShareAmount));
        recivers.reversed();

        return processForPayment(shareGroup, bill, payer, recivers);
    }

    private static List<PaymentInfo> processForPayment(ShareGroup shareGroup, Bill bill,
                                                       List<ExpenseDto> deptors, List<ExpenseDto> recivers) {

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        int i = 0, j = 0;
        while (i < deptors.size() && j < recivers.size()) {
            var depter = deptors.get(i);
            var depterCost = depter.getShareAmount();

            var reciver = recivers.get(j);
            var reciverCost = reciver.getShareAmount();

            var costToPay = Math.min(-depterCost, reciverCost);

            var paymentInfo = buildPaymentInfo(shareGroup, bill, depter, reciver, costToPay);
            paymentInfoList.add(paymentInfo);

            deptors.get(i).setShareAmount(depterCost + costToPay);
            recivers.get(j).setShareAmount(reciverCost - costToPay);

            if (deptors.get(i).getShareAmount() == 0) {
                i++;
            }
            if (recivers.get(j).getShareAmount() == 0) {
                j++;
            }
        }
        return paymentInfoList;
    }

    private static PaymentInfo buildPaymentInfo(ShareGroup shareGroup, Bill bill,
                                                ExpenseDto depter, ExpenseDto reciver, double costToPay) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPayer(depter.getAppUser());
        paymentInfo.setReceiver(reciver.getAppUser());
        paymentInfo.setShareGroup(shareGroup);
        paymentInfo.setAmount(costToPay);
        paymentInfo.setBill(bill);
        return paymentInfo;
    }

    @Getter
    @AllArgsConstructor
    private class ExpenseDto {
        private AppUser appUser;
        private Bill bill;
        @Setter
        private double shareAmount;
    }
}
