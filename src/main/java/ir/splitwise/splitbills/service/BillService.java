package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.*;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
import ir.splitwise.splitbills.repository.BillRepository;
import ir.splitwise.splitbills.repository.ExpenseRepository;
import ir.splitwise.splitbills.repository.PaymentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillService {
    private final UserService userService;
    private final BillRepository billRepository;
    private final ShareGroupService shareGroupService;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ExpenseRepository expenseRepository;
    private final Gson gson;

    @Transactional(rollbackFor = Throwable.class)
    public BaseRequest addBill(AddBillRequest request, AppUser appUser)
            throws UserNotFoundException, ContentNotFoundException, InvalidDataException {
        //request.items() check not be null
        var totalCost = getBillTotalCost(request.items());
        if (totalCost != request.totalCost()) {
            throw new InvalidDataException("totalCost is not match");
        }
        var foundGroup = shareGroupService.findGroupById(request.groupId());
        var payer = userService.findUserById(request.payerId());

        var bill = buildBill(request, foundGroup, payer, appUser);
        var savedBill = billRepository.save(bill);

        var groupCost = foundGroup.getTotalCost();
        foundGroup.setTotalCost(totalCost + groupCost);
        shareGroupService.saveGroupInDb(foundGroup);

//        List<Expense> expenses = addExpense(bill, request.items());
//        List<PaymentInfo> pay = processPayInfo(expenses, foundGroup, bill);
//        paymentInfoRepository.saveAll(pay);
        return new BaseRequest(savedBill.getId());//todo it is necessary?
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modifyBill(ModifyBillRequest request,AppUser modifyer) throws UserNotFoundException, ContentNotFoundException {
        var foundBill = findBillFromDb(request.id());
        var payer = userService.findUserById(request.payerId());

        updateBillParams(request, foundBill, modifyer, payer);
        billRepository.save(foundBill);
        var shareGroup = foundBill.getShareGroup();

        var totalCost = getBillTotalCost(request.items());
        shareGroup.setTotalCost(totalCost);
        shareGroupService.saveGroupInDb(shareGroup);

//        List<Expense> expenses = expenseRepository.finaAllByBillId(foundBill.getId());
//        deleteAllTheseExpense and Payment info
//        calculating the begin
//        List<PaymentInfo> pay = processPayInfo(expenses, shareGroup, foundBill);
//        paymentInfoRepository.saveAll(pay);
        //todo expense and payment info set
    }

    private static double getBillTotalCost(List<ItemRequest> items) {
        double totalCost = 0;
        for (ItemRequest item : items) {
            totalCost += item.getTotalCost();
        }
        return totalCost;
    }

    private void updateBillParams(ModifyBillRequest request, Bill foundBill,
                                  AppUser modifyer, AppUser payer) {

        foundBill.setModifier(modifyer);
        foundBill.setPayer(payer);
        foundBill.setTitle(request.title());
        var items = gson.toJson(request.items());
        foundBill.setItems(items);//todo
    }

    private Bill findBillFromDb(long id) throws ContentNotFoundException {
        return billRepository.findById(id).orElseThrow(() -> new ContentNotFoundException("bill " + id + "not found"));
    }

    private Bill buildBill(AddBillRequest addBillRequest, ShareGroup shareGroup,
                           AppUser payer, AppUser creator) {
        //todo user ModelMapper
        var bill = new Bill();
        bill.setDescription(addBillRequest.description());
        bill.setTitle(addBillRequest.title());
        bill.setPayer(payer);
        bill.setItems(gson.toJson(addBillRequest.items()));
        bill.setTotalCost(addBillRequest.totalCost());
        bill.setCreator(creator);
        bill.setShareGroup(shareGroup);
        return bill;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteBill(long id) throws ContentNotFoundException {
        var founfBill = findBillFromDb(id);
        var deletedBillCost = founfBill.getTotalCost();
        billRepository.delete(founfBill);

        var shareGroup = updateShareGroupTotalCost(founfBill, deletedBillCost);
        shareGroupService.saveGroupInDb(shareGroup);
    }

    private static ShareGroup updateShareGroupTotalCost(Bill founfBill, double deletedBillCost) {
        var shareGroup = founfBill.getShareGroup();
        double shareGroupTotalCost = shareGroup.getTotalCost();
        shareGroupTotalCost -= deletedBillCost;
        shareGroup.setTotalCost(shareGroupTotalCost);
        return shareGroup;
    }

    @Transactional
    public List<PaymentResponse> getPayInfoOfGroup(long shareGroupId) throws ContentNotFoundException {
        var foundGroup = shareGroupService.findGroupById(shareGroupId);
        List<PaymentInfo> payInfoOfGroup = getPayInfoOfGroup(foundGroup);
        return getPaymentResponses(payInfoOfGroup);
    }

    public List<PaymentInfo> getPayInfoOfGroup(ShareGroup shareGroup) throws ContentNotFoundException {
        var deptOfGroup = getALlDeptOfGroup(shareGroup.getId());
        List<Map.Entry<AppUser, Double>> deptors = new ArrayList<>();
        List<Map.Entry<AppUser, Double>> recivers = new ArrayList<>();

        for (var appUserDoubleEntry : deptOfGroup.entrySet()) {
            var dept = appUserDoubleEntry.getValue();

            if (dept < 0) {
                deptors.add(appUserDoubleEntry);
            } else {
                recivers.add(appUserDoubleEntry);
            }
        }
        deptors.sort(Comparator.comparingDouble(Map.Entry::getValue));
        recivers.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        var paymentInfoList = processPayment(shareGroup, deptors, recivers);
        paymentInfoRepository.saveAll(paymentInfoList);//todo active batch in application
        return paymentInfoList;
    }

    private List<PaymentInfo> processPayment(ShareGroup shareGroup, List<Map.Entry<AppUser, Double>> deptors, List<Map.Entry<AppUser, Double>> recivers) {

        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        int i = 0, j = 0;
        while (i < deptors.size() && j < recivers.size()) {
            var depter = deptors.get(i);
            var depterCost = depter.getValue();

            var reciver = recivers.get(j);
            var reciverCost = reciver.getValue();

            var costToPay = Math.min(-depterCost, reciverCost);
            var paymentInfo = buildPaymentInfo(depter, reciver, shareGroup, costToPay);
            paymentInfoList.add(paymentInfo);

            deptors.get(i).setValue(depterCost + costToPay);
            recivers.get(i).setValue(reciverCost - costToPay);

            if (deptors.get(i).getValue() == 0) {
                i++;
            }
            if (recivers.get(j).getValue() == 0) {
                j++;
            }
        }
        return paymentInfoList;
    }

    private static PaymentInfo buildPaymentInfo(Map.Entry<AppUser, Double> depter,
                                                Map.Entry<AppUser, Double> reciver,
                                                ShareGroup shareGroup,
                                                double costToPay
    ) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPayer(depter.getKey());
        paymentInfo.setReceiver(reciver.getKey());
        paymentInfo.setShareGroup(shareGroup);
        paymentInfo.setAmount(costToPay);
        return paymentInfo;
    }

    public List<PaymentResponse> getPayInfoOfUser(long groupId, AppUser requester) {
        //calculate first
        List<PaymentInfo> allByIdAndShareGroup = paymentInfoRepository.findAllByIdAndShareGroup(requester.getId(), groupId);
        return getPaymentResponses(allByIdAndShareGroup);
    }

    private static List<PaymentResponse> getPaymentResponses(List<PaymentInfo> payInfoOfGroup) {
        return payInfoOfGroup.stream().map(paymentInfo ->//todo fix the load of all info
                new PaymentResponse(new AppUserResponse(paymentInfo.getPayer().getUsername()),
                        new AppUserResponse(paymentInfo.getReceiver().getUsername())
                        , paymentInfo.getAmount())).collect(Collectors.toList());
    }

    public List<Bill> findAllBillOfGroup(long groupId) {
        return billRepository.findAllByGroupId(groupId);
    }






    public Map<AppUser, Double> getALlDeptOfGroup(long groupId) throws ContentNotFoundException {
        ShareGroup group = shareGroupService.findGroupById(groupId);
        double totalCost = group.getTotalCost();//todo validate

        Map<AppUser, Double> deptOfGroup = new HashMap<>();
        List<Bill> billList = findAllBillOfGroup(groupId);
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
        return deptOfGroup;
    }
}
