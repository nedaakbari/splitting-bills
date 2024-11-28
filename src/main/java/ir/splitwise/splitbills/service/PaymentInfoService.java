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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentInfoService implements DataResult {
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
        List<ExpenseDto> creditorList = new ArrayList<>();
        for (ExpenseDto expens : expenses) {
            if (expens.getShareAmount() < 0) {
                payer.add(expens);
            } else {
                creditorList.add(expens);
            }
        }

        payer.sort(Comparator.comparingDouble(ExpenseDto::getShareAmount));
        creditorList.sort(Comparator.comparingDouble(ExpenseDto::getShareAmount));
        creditorList.reversed();

        return processForPayment(shareGroup, bill, payer, creditorList);
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


    private <T> T execute(long groupId, ExecutorFunction<T> executor) {
        return executor.apply();
    }

    @Override
    public DataJsonResult executeJson() {
        return null;
    }

    public List<PaymentResponse> getPayInfoOfUser(PaymentRequest request, AppUser requester) {
        var allByIdAndShareGroup = request.dept() ?
                paymentInfoRepository.findAllUserRecivePaymentInfo(requester.getId(), request.groupId())
                : paymentInfoRepository.findAllUserDeptPaymentInfo(requester.getId(), request.groupId());
        return getPaymentResponses(allByIdAndShareGroup);
    }

    @Override
    public DataExcelResult executeExcel(PaymentRequest request, AppUser requester) {
        var allByIdAndShareGroup = request.dept() ?
                paymentInfoRepository.findAllUserRecivePaymentInfo(requester.getId(), request.groupId())
                : paymentInfoRepository.findAllUserDeptPaymentInfo(requester.getId(), request.groupId());

        Workbook workbook = new XSSFWorkbook();
        Sheet paymentData = workbook.createSheet("paymentData");
        Row headerRow = paymentData.createRow(0);
        headerRow.createCell(0).setCellValue("Column 1");
        headerRow.createCell(1).setCellValue("Column 2");
    }


    @FunctionalInterface
    private interface ExecutorFunction<R> {
        R apply(long groupId,)//?
    }

    @Getter
    @AllArgsConstructor
    private static class ExpenseDto {
        private AppUser appUser;
        private Bill bill;
        @Setter
        private double shareAmount;
    }


    private DataJsonResult executeJson(long groupId) {
        return execute(groupId, DataResult::executeJson);
    }

    public DataExcelResult executeExcel(long groupId) {
        return execute(groupId, DataResult::executeExcel);
    }
}
