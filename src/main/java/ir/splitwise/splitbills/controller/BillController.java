package ir.splitwise.splitbills.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.splitwise.splitbills.CheckAppUser;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.models.BaseRequest;
import ir.splitwise.splitbills.models.ModifyBillRequest;
import ir.splitwise.splitbills.service.AuthenticationService;
import ir.splitwise.splitbills.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bill APIs",
        description = "This category includes APIs related to bill")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bill")
public class BillController {
    private final BillService billService;

    @PostMapping("/add")
    @Operation(//            summary = "add "
            description = "this api used for adding a bill to a group"
    )
    public BaseRequest addBillToAGroup(@RequestBody AddBillRequest addBillRequest, Authentication authentication)
            throws UserNotFoundException, ContentNotFoundException, InvalidDataException {
        AppUser appUser = CheckAppUser.checkUserInstance(authentication);
        return billService.addBill(addBillRequest,appUser);
    }

    @PostMapping("/modify")
    @Operation(description = "this api used for modify bill that already added, include items,expense of that and ...")
    public void modifyBill(@RequestBody ModifyBillRequest modifyBillRequest)
            throws UserNotFoundException, ContentNotFoundException {

        billService.modifyBill(modifyBillRequest);
    }

    @PostMapping("/delete")
    @Operation(description = "this api used for delete a bill that already added to a group")
    public void deleteBill(@RequestBody BaseRequest baseRequest)
            throws ContentNotFoundException {

        billService.deleteBill(baseRequest.id());
    }

}
