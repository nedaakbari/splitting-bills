package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.models.ModifyBillRequest;
import ir.splitwise.splitbills.repository.BillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {
    private final UserService userService;
    private final BillRepository billRepository;
    private final ShareGroupService shareGroupService;

    @Transactional(rollbackOn = Throwable.class)
    public void addBill(AddBillRequest request) throws UserNotFoundException, ContentNotFoundException {
        var creator = userService.findUserById(request.payer());
        var payer = userService.findUserById(request.payer());
        var bill = buildBill(request, payer, creator);
        Bill savedBill = billRepository.save(bill);

        long groupId = request.groupId();
        ShareGroup foundGroup = shareGroupService.findGroupById(groupId);
        List<Bill> billList = foundGroup.getBillList();
        billList.add(savedBill);
        foundGroup.setBillList(billList);
        shareGroupService.saveGroupInDb(foundGroup);
    }

    public void modifyBill(ModifyBillRequest request) throws UserNotFoundException, ContentNotFoundException {
        var foundBill = getBillFromDb(request.id());

        var modifyer = userService.findUserById(1);//todo get from spring
        var payer = userService.findUserById(request.payer());

        foundBill.setModifier(modifyer);
        foundBill.setPayer(payer);
        foundBill.setTitle(request.title());
        foundBill.setDescription(request.description());
        billRepository.save(foundBill);
    }

    private Bill getBillFromDb(long id) throws ContentNotFoundException {
        return billRepository.findById(id).orElseThrow(() -> new ContentNotFoundException(""));
    }

    private static Bill buildBill(AddBillRequest addBillRequest, AppUser payer, AppUser creator) {
        Bill bill = new Bill();
        bill.setDescription(addBillRequest.description());
        bill.setTitle(addBillRequest.title());
        bill.setPayer(payer);
        bill.setItems(addBillRequest.items());
//        bill.setTotalCost(addBillRequest.totalCost());//todo should be validate users or sum the costs??
        bill.setCreator(creator);
        return bill;
    }


}
