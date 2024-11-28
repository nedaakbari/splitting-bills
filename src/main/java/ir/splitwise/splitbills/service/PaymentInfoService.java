package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AppUserResponse;
import ir.splitwise.splitbills.models.ItemRequest;
import ir.splitwise.splitbills.models.PaymentRequest;
import ir.splitwise.splitbills.models.PaymentResponse;
import ir.splitwise.splitbills.repository.PaymentInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;
    private final BillService billService;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ShareGroupService shareGroupService;
    private final Gson gson;
    private static final Type itemList = new TypeToken<List<ItemRequest>>() {
    }.getType();

    @Transactional(rollbackFor = Throwable.class)
    public List<PaymentResponse> getPayInfoOfGroup(long groupId) throws ContentNotFoundException, UserNotFoundException {

        ShareGroup foundGroup = shareGroupService.findGroupById(groupId);
        List<Bill> billListOfAGroup = billService.findAllBillOfGroup(groupId);
        expenseService.deleteAllByBillIds(billListOfAGroup.stream().map(Bill::getId).toList());
        paymentInfoRepository.deleteAllByGroupId(groupId);

        List<ExpenseDto> expenseList = new ArrayList<>();
        for (Bill bill : billListOfAGroup) {
            expenseList.addAll(getExpenseDtoList(bill));
        }

        var paymentInfo = processForPayment(expenseList, foundGroup);
        List<PaymentInfo> paymentInfoList = new ArrayList<>(paymentInfo);

        var savedPayment = paymentInfoRepository.saveAll(paymentInfoList);
        return savedPayment.stream().map(x -> new PaymentResponse(new AppUserResponse(x.getPayer().getUsername()),
                new AppUserResponse(x.getReceiver().getUsername()), x.getAmount())).toList();

    }

    List<PaymentInfo> processForPayment(List<ExpenseDto> expenses, ShareGroup shareGroup) {
        Map<AppUser, Double> finalExpense = new HashMap<>();
        for (ExpenseDto expens : expenses) {
            AppUser appUser = expens.getAppUser();
            double shareAmount = expens.getShareAmount();
            double userAmount = finalExpense.get(appUser) == null ? 0 : finalExpense.get(appUser);
            finalExpense.put(appUser, userAmount + shareAmount);
        }

        List<Pair> debtors = finalExpense.entrySet().stream()
                .filter(x -> x.getValue() < 0)
                .map(x -> new Pair(x.getKey(), x.getValue()))
                .toList();
        List<Pair> creditors = finalExpense.entrySet().stream()
                .filter(x -> x.getValue() > 0)
                .map(x -> new Pair(x.getKey(), x.getValue()))
                .toList();


        return processForPayment(shareGroup, debtors, creditors);
    }

    private static List<PaymentInfo> processForPayment(ShareGroup shareGroup,
                                                       List<Pair> debtorList,
                                                       List<Pair> creditorList) {

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        int i = 0, j = 0;
        while (i < debtorList.size() && j < creditorList.size()) {
            var debtor = debtorList.get(i);
            var debtorCost = debtor.getAmount();

            var creditor = creditorList.get(j);
            var creditorCost = creditor.getAmount();

            var costToPay = Math.min(-debtorCost, creditorCost);
            var paymentInfo = buildPaymentInfo(shareGroup, debtor.getAppUser(), creditor.getAppUser(), costToPay);
            paymentInfoList.add(paymentInfo);

            debtorList.get(i).setAmount(debtorCost + costToPay);
            creditorList.get(j).setAmount(creditorCost - costToPay);

            if (debtorList.get(i).getAmount() == 0) {
                i++;
            }
            if (creditorList.get(j).getAmount() == 0) {
                j++;
            }
        }
        return paymentInfoList;
    }

    private static PaymentInfo buildPaymentInfo(ShareGroup shareGroup,
                                                AppUser deptor, AppUser creditor,
                                                double costToPay) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPayer(deptor);
        paymentInfo.setReceiver(creditor);
        paymentInfo.setShareGroup(shareGroup);
        paymentInfo.setAmount(costToPay);
        return paymentInfo;
    }

    public List<PaymentResponse> getPayInfoOfUser(PaymentRequest request, AppUser requester) {
        var allByIdAndShareGroup = request.dept() ?
                paymentInfoRepository.findAllUserDeptPaymentInfo(requester.getId(), request.groupId())
                : paymentInfoRepository.findAllUserReciverPaymentInfo(requester.getId(), request.groupId());
        return getPaymentResponses(allByIdAndShareGroup);
    }

    private static List<PaymentResponse> getPaymentResponses(List<PaymentInfo> payInfoOfGroup) {
        return payInfoOfGroup.stream().map(paymentInfo ->
                new PaymentResponse(new AppUserResponse(paymentInfo.getPayer().getUsername()),
                        new AppUserResponse(paymentInfo.getReceiver().getUsername())
                        , paymentInfo.getAmount())).collect(Collectors.toList());
    }

    private List<ExpenseDto> getExpenseDtoList(Bill bill) throws UserNotFoundException {
        List<ItemRequest> itemRequest = gson.fromJson(bill.getItems(), itemList);
        var expenses = expenseService.addExpense(bill, itemRequest);
        return expenses.stream()
                .map(expense -> new ExpenseDto(expense.getAppUser(), expense.getBill(), expense.getShareAmount()))
                .toList();
    }

    @Getter
    @AllArgsConstructor
    private static class ExpenseDto {
        private AppUser appUser;
        private Bill bill;
        @Setter
        private double shareAmount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Pair {
        private AppUser appUser;
        private double amount;
    }
}
