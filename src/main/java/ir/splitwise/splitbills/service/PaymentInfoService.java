package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentInfoService {
    private final ExpenseService expenseService;

    public List<PaymentInfo> processingPayInfo(long groupId) throws ContentNotFoundException {
        Map<AppUser, Double> deptOfGroup = expenseService.getALlDeptOfGroup(groupId);
        for (Map.Entry<AppUser, Double> appUserDoubleEntry : deptOfGroup.entrySet()) {
            AppUser appUser = appUserDoubleEntry.getKey();
            Double dept = appUserDoubleEntry.getValue();

            if (dept < 0) {//he is reciver

            } else {//he is payer

            }
        }
        return null;
    }

}
