package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.models.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;

    public List<PaymentInfo> getPayInfoOfGroup(long groupId) throws ContentNotFoundException {
        var deptOfGroup = expenseService.getALlDeptOfGroup(groupId);
        var deptors = new ArrayList<>();
        var recivers = new ArrayList<>();

        for (var appUserDoubleEntry : deptOfGroup.entrySet()) {
            var dept = appUserDoubleEntry.getValue();

            if (dept < 0) {
                deptors.add(appUserDoubleEntry);
            } else {
                recivers.add(appUserDoubleEntry);
            }
        }




        return null;
    }


    private void processPayment() {

    }

    public List<PaymentResponse> getPayInfoOfUser(long groupId, AppUser requester) throws ContentNotFoundException {
        //pay to who
        //receive from who
        //just for him
        return null;
    }

}
