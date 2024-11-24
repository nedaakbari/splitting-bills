package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.Item;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillService {
    private final UserService userService;
    private final BillRepository billRepository;

    public void addBill(AddBillRequest addBillRequest) throws UserNotFoundException {
        var creator = userService.findUserById(addBillRequest.payer());
        var payer = userService.findUserById(addBillRequest.payer());
        var bill = buildBill(addBillRequest, payer, creator);
        billRepository.save(bill);

    }

    private static Bill buildBill(AddBillRequest addBillRequest, AppUser payer, AppUser creator) {
        Bill bill = new Bill();
        bill.setDescription(addBillRequest.description());
        bill.setTitle(addBillRequest.title());
        bill.setPayer(payer);
        bill.setItems(addBillRequest.items());
        bill.setTotalCost(addBillRequest.totalCost());//todo should be validate users or sum the costs??
        bill.setCreator(creator);
        return bill;
    }
}
