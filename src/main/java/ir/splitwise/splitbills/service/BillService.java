package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.models.BaseRequest;
import ir.splitwise.splitbills.models.ItemRequest;
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


    public BaseRequest addBill(AddBillRequest request)
            throws UserNotFoundException, ContentNotFoundException, InvalidException {
        long groupId = request.groupId();
        var foundGroup = shareGroupService.findGroupById(groupId);

        var creator = userService.findUserById(request.payerId());
        var payer = userService.findUserById(request.payerId());
        var bill = buildBill(request, foundGroup, payer, creator);
        var savedBill = billRepository.save(bill);

        return new BaseRequest(savedBill.getId());//todo it is necessary?
    }

    @Transactional(rollbackOn = Throwable.class)
    public void modifyBill(ModifyBillRequest request) throws UserNotFoundException, ContentNotFoundException {
        var foundBill = findBillFromDb(request.id());

        var modifyer = userService.findUserById(1);//todo get from spring
        var payer = userService.findUserById(request.payerId());

        updateBillParams(request, foundBill, modifyer, payer);
        billRepository.save(foundBill);

        List<ItemRequest> items = request.items();
        double totalCost = 0;
        for (ItemRequest item : items) {
            totalCost += item.getCost();
        }
        ShareGroup shareGroup = foundBill.getShareGroup();
        shareGroup.setTotalCost(totalCost);
        shareGroupService.saveGroupInDb(shareGroup);
    }

    private static void updateBillParams(ModifyBillRequest request, Bill foundBill, AppUser modifyer, AppUser payer) {
        foundBill.setModifier(modifyer);
        foundBill.setPayer(payer);
        foundBill.setTitle(request.title());
        String items = new Gson().toJson(request.items());
        foundBill.setItems(items);//todo
    }

    private Bill findBillFromDb(long id) throws ContentNotFoundException {
        return billRepository.findById(id).orElseThrow(() -> new ContentNotFoundException("bill " + id + "not found"));
    }

    private static Bill buildBill(AddBillRequest addBillRequest, ShareGroup shareGroup,
                                  AppUser payer, AppUser creator) throws InvalidException {
        Bill bill = new Bill();
        bill.setDescription(addBillRequest.description());
        bill.setTitle(addBillRequest.title());
        bill.setPayer(payer);
        List<ItemRequest> items = addBillRequest.items();
        bill.setItems(new Gson().toJson(items));
        bill.setTotalCost(addBillRequest.totalCost());//todo should be validate users or sum the costs??check part cost not be more then totalCost
        bill.setCreator(creator);
        bill.setShareGroup(shareGroup);
        return bill;
    }

    public void deleteBill(long id) throws ContentNotFoundException {
        Bill founfBill = findBillFromDb(id);
        billRepository.delete(founfBill);//todo it is required to keep it for history? i dont think so
    }
}
