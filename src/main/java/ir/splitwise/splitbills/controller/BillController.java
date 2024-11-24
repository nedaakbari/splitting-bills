package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bill")
public class BillController {
    private final BillService billService;

    @PostMapping("/add")
    public void addBillToAGroup(@RequestBody AddBillRequest addBillRequest) {
        billService.addBill(addBillRequest);
    }

    @PostMapping("/modify")
    public void modifyBill() {
        //todo
    }

}
