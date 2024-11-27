package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
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
    private final ExpenseService expenseService;

    @Transactional(rollbackOn = Throwable.class)
    public BaseRequest addBill(AddBillRequest request,AppUser appUser)
            throws UserNotFoundException, ContentNotFoundException, InvalidDataException {
        List<ItemRequest> items = request.items();
        double totalCost = getBillTotalCost(items);

        if (totalCost!= request.totalCost()){
            throw new InvalidDataException("totalCost is not match");
        }

        long groupId = request.groupId();
        var foundGroup = shareGroupService.findGroupById(groupId);
        var payer = userService.findUserById(request.payerId());
        var bill = buildBill(request, foundGroup, payer, appUser);



        var savedBill = billRepository.save(bill);
        expenseService.addExpense(bill, request.items());


        double groupCost = foundGroup.getTotalCost();
        foundGroup.setTotalCost(totalCost + groupCost);
        shareGroupService.saveGroupInDb(foundGroup);
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
        double totalCost = getBillTotalCost(items);
        ShareGroup shareGroup = foundBill.getShareGroup();
        shareGroup.setTotalCost(totalCost);
        shareGroupService.saveGroupInDb(shareGroup);
    }

    private static double getBillTotalCost(List<ItemRequest> items) {
        for (ItemRequest item : items) {
            List<UserItem> userItems = item.getUserItems();
            List<Long> list = userItems.stream().map(UserItem::getUserId).toList();
        }
        double totalCost = 0;
        for (ItemRequest item : items) {
            totalCost += item.getTotalCost();
        }
        return totalCost;
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
                                  AppUser payer, AppUser creator) throws InvalidDataException {
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
