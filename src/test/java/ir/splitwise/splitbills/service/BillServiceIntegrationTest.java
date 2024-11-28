package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Bill;
import ir.splitwise.splitbills.entity.ShareGroup;
import ir.splitwise.splitbills.exceptions.ContentNotFoundException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.*;
import ir.splitwise.splitbills.models.enumeration.GroupMode;
import ir.splitwise.splitbills.repository.BillRepository;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import ir.splitwise.splitbills.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BillServiceIntegrationTest {
    @Autowired
    BillService billService;
    @Autowired
    BillRepository billRepository;
    @Autowired
    UserService userService;
    UserRepository userRepository;
    @Autowired
    ShareGroupService shareGroupService;
    @Autowired
    ShareGroupRepository shareGroupRepository;
    @Autowired
    Gson gson;
    private AppUser appUser1;
    private AppUser appUser2;
    private AppUser appUser3;
    private AppUser appUser4;
    private ShareGroup group;

    @BeforeEach
    void setUp() {
        appUser1 = AppUser.builder().email("test1@gmail").build();
        appUser2 = AppUser.builder().email("test2@gmail").build();
        appUser3 = AppUser.builder().email("test3@gmail").build();
        appUser4 = AppUser.builder().email("test4@gmail").build();

        group = ShareGroup.builder().groupMode(GroupMode.OWNER_ONLY)
                .members(List.of(appUser1, appUser2, appUser3, appUser4))
                .title("trip").owner(appUser1).description("best trip ever").
                build();

        shareGroupRepository.save(group);
        userRepository.saveAll(List.of(appUser1, appUser2, appUser3, appUser4));
    }

    @Test
    void addBillTest_theSuccess() throws UserNotFoundException, ContentNotFoundException, InvalidDataException {
        List<ItemRequest> itemRequestList = new ArrayList<>();

        UserItem userItem1 = new UserItem(appUser1.getId(), 1);
        UserItem userItem2 = new UserItem(appUser2.getId(), 1);
        UserItem userItem3 = new UserItem(appUser3.getId(), 1);
        UserItem userItem4 = new UserItem(appUser4.getId(), 1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setName("malliat");
        itemRequest1.setCount(1);
        itemRequest1.setEqualShare(true);
        itemRequest1.setUserItems(List.of(userItem1, userItem2, userItem3, userItem4));
        itemRequest1.setTotalCost(2000);

        UserItem userItem5 = new UserItem(appUser1.getId(), 2);
        UserItem userItem6 = new UserItem(appUser2.getId(), 1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setName("late");
        itemRequest2.setCount(3);
        itemRequest2.setEqualShare(false);
        itemRequest2.setUserItems(List.of(userItem5, userItem6));
        itemRequest2.setTotalCost(3000);

        UserItem userItem7 = new UserItem(appUser3.getId(), 1);
        UserItem userItem8 = new UserItem(appUser4.getId(), 2);

        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setName("hot choclate");
        itemRequest3.setCount(3);
        itemRequest3.setEqualShare(false);
        itemRequest3.setUserItems(List.of(userItem7, userItem8));
        itemRequest3.setTotalCost(2000);

        itemRequestList.add(itemRequest1);
        itemRequestList.add(itemRequest2);
        itemRequestList.add(itemRequest3);

        AddBillRequest addBillRequest = new AddBillRequest(group.getId(), "bill test",
                "coffe", 7000, 1, itemRequestList);

        BaseRequestResponse response = billService.addBill(addBillRequest, appUser1);
        Assertions.assertNotNull(response);
        Bill bill = billRepository.findById(response.id()).orElseThrow();
        ShareGroup foundGroup = shareGroupRepository.findById(group.getId()).orElseThrow();
        boolean b = foundGroup.getTotalCost() == 7000;
    }

}
