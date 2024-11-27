package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.entity.PaymentInfo;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.models.BaseRequest;
import ir.splitwise.splitbills.service.PaymentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paymentInfo")
public class PaymentInfoController {
    private final PaymentInfoService paymentInfoService;


    @PostMapping("/get-all-for-group")
    public List<PaymentInfo> getAllPaymentInfo(@RequestBody BaseRequest request) throws ContentNotFoundException {
        return paymentInfoService.processingPayInfo(request.id());
    }
}
