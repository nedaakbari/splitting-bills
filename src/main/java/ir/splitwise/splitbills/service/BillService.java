package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.AddBillRequest;
import ir.splitwise.splitbills.models.BaseRequestResponse;
import ir.splitwise.splitbills.models.ItemRequest;
import ir.splitwise.splitbills.models.ModifyBillRequest;
import ir.splitwise.splitbills.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {
    private final UserService userService;
    private final BillRepository billRepository;
    private final ShareGroupService shareGroupService;
    private final Gson gson;

    @Transactional(rollbackFor = Throwable.class)
    public BaseRequestResponse addBill(AddBillRequest request, AppUser appUser)
            throws UserNotFoundException, ContentNotFoundException, InvalidDataException {
        var totalCost = getBillTotalCost(request.items());
        if (totalCost != request.totalCost()) {
            throw new InvalidDataException("totalCost is not match");
        }
        var foundGroup = shareGroupService.findGroupById(request.groupId());
        var payer = userService.findUserById(request.payerId());

        var bill = buildBill(request, foundGroup, payer, appUser);
        var savedBill = billRepository.save(bill);

        var groupCost = foundGroup.getTotalCost();
        foundGroup.setTotalCost(totalCost + groupCost);
        shareGroupService.saveGroupInDb(foundGroup);
        return new BaseRequestResponse(savedBill.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBill(ModifyBillRequest request, AppUser modifyer) throws UserNotFoundException, ContentNotFoundException {
        var foundBill = findBillFromDb(request.id());
        var payer = userService.findUserById(request.payerId());

        updateBillParams(request, foundBill, modifyer, payer);
        billRepository.save(foundBill);
        var shareGroup = foundBill.getShareGroup();

        var totalCost = getBillTotalCost(request.items());
        shareGroup.setTotalCost(totalCost);
        shareGroupService.saveGroupInDb(shareGroup);
    }

    private static double getBillTotalCost(List<ItemRequest> items) {
        double totalCost = 0;
        for (ItemRequest item : items) {
            totalCost += item.getTotalCost();
        }
        return totalCost;
    }

    private void updateBillParams(ModifyBillRequest request, Bill foundBill,
                                  AppUser modifyer, AppUser payer) {

        foundBill.setModifier(modifyer);
        foundBill.setPayer(payer);
        foundBill.setTitle(request.title());
        var items = gson.toJson(request.items());
        foundBill.setItems(items);
    }

    private Bill findBillFromDb(long id) throws ContentNotFoundException {
        return billRepository.findById(id).orElseThrow(() -> new ContentNotFoundException("bill " + id + "not found"));
    }

    private Bill buildBill(AddBillRequest addBillRequest, ShareGroup shareGroup,
                           AppUser payer, AppUser creator) {
        //todo user ModelMapper or map struct
        var bill = new Bill();
        bill.setDescription(addBillRequest.description());
        bill.setTitle(addBillRequest.title());
        bill.setPayer(payer);
        bill.setItems(gson.toJson(addBillRequest.items()));
        bill.setTotalCost(addBillRequest.totalCost());
        bill.setCreator(creator);
        bill.setShareGroup(shareGroup);
        return bill;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteBill(long id) throws ContentNotFoundException {
        var founfBill = findBillFromDb(id);
        var deletedBillCost = founfBill.getTotalCost();
        billRepository.delete(founfBill);

        var shareGroup = updateShareGroupTotalCost(founfBill, deletedBillCost);
        shareGroupService.saveGroupInDb(shareGroup);
    }

    private static ShareGroup updateShareGroupTotalCost(Bill founfBill, double deletedBillCost) {
        var shareGroup = founfBill.getShareGroup();
        double shareGroupTotalCost = shareGroup.getTotalCost();
        shareGroupTotalCost -= deletedBillCost;
        shareGroup.setTotalCost(shareGroupTotalCost);
        return shareGroup;
    }

    public List<Bill> findAllBillOfGroup(long groupId) {
        return billRepository.findAllByGroupId(groupId);
    }
}
