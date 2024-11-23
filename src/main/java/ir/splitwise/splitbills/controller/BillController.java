package ir.splitwise.splitbills.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bill")
public class BillController {

    @PostMapping("/add")
    public void addBill() {
        //todo
    }
    @PostMapping("/modify")
    public void modifyBill() {
        //todo
    }

}
