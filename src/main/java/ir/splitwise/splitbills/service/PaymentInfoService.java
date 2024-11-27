package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.models.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;

    public List<PaymentInfo> getPayInfoOfGroup(long groupId) throws ContentNotFoundException {
        Map<AppUser, Double> deptOfGroup = expenseService.getALlDeptOfGroup(groupId);
        List<Map.Entry<AppUser, Double>> deptors = new ArrayList<>();
        List<Map.Entry<AppUser, Double>> recivers = new ArrayList<>();

        for (Map.Entry<AppUser, Double> appUserDoubleEntry : deptOfGroup.entrySet()) {
            var dept = appUserDoubleEntry.getValue();

            if (dept < 0) {
                deptors.add(appUserDoubleEntry);
            } else {
                recivers.add(appUserDoubleEntry);
            }
        }
        deptors.sort(Comparator.comparingDouble(Map.Entry::getValue));
        recivers.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));


        return null;
    }


    private void processPayment(Bill bill,
                                List<Map.Entry<AppUser, Double>> deptors,
                                List<Map.Entry<AppUser, Double>> recivers) {
        List<String> calculate = new ArrayList<>();
        int i = 0, j = 0;
        while (i < deptors.size() && j < recivers.size()) {
            Map.Entry<AppUser, Double> depter = deptors.get(i);
            Double depterCost = depter.getValue();

            Map.Entry<AppUser, Double> reciver = recivers.get(i);
            Double reciverCost = reciver.getValue();


            double amountToPay = Math.min(depterCost, reciverCost);
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPayer(depter.getKey());
            paymentInfo.setReceiver(reciver.getKey());
            paymentInfo.setBillList(bill);
        }


    }

    public List<PaymentResponse> getPayInfoOfUser(long groupId, AppUser requester) throws ContentNotFoundException {
        //pay to who
        //receive from who
        //just for him
        return null;
    }

}
