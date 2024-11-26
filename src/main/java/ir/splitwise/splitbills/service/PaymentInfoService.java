package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.models.AppUserResponse;
import ir.splitwise.splitbills.models.PaymentResponse;
import ir.splitwise.splitbills.repository.PaymentInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ShareGroupService shareGroupService;

    @Transactional
    public List<PaymentResponse> getPayInfoOfGroup(long shareGroupId) throws ContentNotFoundException {
        var foundGroup = shareGroupService.findGroupById(shareGroupId);
        List<PaymentInfo> payInfoOfGroup = getPayInfoOfGroup(foundGroup);

        return getPaymentResponses(payInfoOfGroup);
    }

    public List<PaymentInfo> getPayInfoOfGroup(ShareGroup shareGroup) throws ContentNotFoundException {
        var deptOfGroup = expenseService.getALlDeptOfGroup(shareGroup.getId());
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

}
