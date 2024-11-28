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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        for (Bill bill : billListOfAGroup) {
            var list = getExpenseDtoList(bill);
            var paymentInfo = processForPayment(list, foundGroup, bill);
            paymentInfoList.addAll(paymentInfo);
        }
        paymentInfoRepository.deleteAllByBillIds(billListOfAGroup.stream().map(Bill::getId).toList());
        var savedPayment = paymentInfoRepository.saveAll(paymentInfoList);
        return savedPayment.stream().map(x -> new PaymentResponse(new AppUserResponse(x.getPayer().getUsername()),
                new AppUserResponse(x.getReceiver().getUsername()), x.getAmount())).toList();

    }

    @Transactional(readOnly = true)
    List<PaymentInfo> processForPayment(List<ExpenseDto> expenses, ShareGroup shareGroup, Bill bill) {
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
                                                       List<ExpenseDto> deptorList, List<ExpenseDto> creditorList) {

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        int i = 0, j = 0;
        while (i < deptorList.size() && j < creditorList.size()) {
            var deptor = deptorList.get(i);
            var deptorCost = deptor.getShareAmount();

            var creditor = creditorList.get(j);
            var creditorCost = creditor.getShareAmount();

            var costToPay = Math.min(-deptorCost, creditorCost);

            var paymentInfo = buildPaymentInfo(shareGroup, bill, deptor, creditor, costToPay);
            paymentInfoList.add(paymentInfo);

            deptorList.get(i).setShareAmount(deptorCost + costToPay);
            creditorList.get(j).setShareAmount(creditorCost - costToPay);

            if (deptorList.get(i).getShareAmount() == 0) {
                i++;
            }
            if (creditorList.get(j).getShareAmount() == 0) {
                j++;
            }
        }
        return paymentInfoList;
    }

    private static PaymentInfo buildPaymentInfo(ShareGroup shareGroup, Bill bill,
                                                ExpenseDto deptor, ExpenseDto creditor, double costToPay) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPayer(deptor.getAppUser());
        paymentInfo.setReceiver(creditor.getAppUser());
        paymentInfo.setShareGroup(shareGroup);
        paymentInfo.setAmount(costToPay);
        paymentInfo.setBill(bill);
        return paymentInfo;
    }

    public List<PaymentResponse> getPayInfoOfUser(PaymentRequest request, AppUser requester) {
        var allByIdAndShareGroup = request.dept() ?
                paymentInfoRepository.findAllUserRecivePaymentInfo(requester.getId(), request.groupId())
                : paymentInfoRepository.findAllUserDeptPaymentInfo(requester.getId(), request.groupId());
        return getPaymentResponses(allByIdAndShareGroup);
    }

    private static List<PaymentResponse> getPaymentResponses(List<PaymentInfo> payInfoOfGroup) {
        return payInfoOfGroup.stream().map(paymentInfo ->//todo fix the load of all info
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
}
