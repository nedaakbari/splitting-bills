package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.CheckAppUser;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.BaseRequestResponse;
import ir.splitwise.splitbills.models.PaymentRequest;
import ir.splitwise.splitbills.models.PaymentResponse;
import ir.splitwise.splitbills.service.PaymentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Payment APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/paymentInfo")
public class PaymentInfoController {
    private final PaymentInfoService paymentInfoService;

    @Operation(description = "this api used for calculating expense and show who should pay to another one in group")
    @PostMapping("/get-all-for-group")
    public List<PaymentResponse> getAllPaymentInfo(@RequestBody BaseRequestResponse request)
            throws ContentNotFoundException, UserNotFoundException {

        return paymentInfoService.getPayInfoOfGroup(request.id());
    }

    @Operation(description = "this api used for calculating expense of a user")
    @PostMapping("/get-all-for-user")
    public List<PaymentResponse> getAllPaymentInfoOfUser(@RequestBody PaymentRequest request, Authentication authentication)
            throws UserNotFoundException {

        var appUser = CheckAppUser.checkUserInstance(authentication);
        return paymentInfoService.getPayInfoOfUser(request, appUser);
    }
}
