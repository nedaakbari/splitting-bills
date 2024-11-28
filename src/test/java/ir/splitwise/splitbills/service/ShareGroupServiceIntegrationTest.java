package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.BaseRequestResponse;
import ir.splitwise.splitbills.models.ShareGroupRequest;
import ir.splitwise.splitbills.repository.ShareGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ShareGroupServiceIntegrationTest {
    @Autowired
    private ShareGroupService shareGroupService;
    @Autowired
    private ShareGroupRepository shareGroupRepository;
    @Autowired
    private UserService userService;

    @Test
    void addShareGroupTest() throws UserNotFoundException {
        ShareGroupRequest ramsarTrip =
                new ShareGroupRequest("trip", List.of(1L, 2L, 3L, 4L), "the best trip ever", true);
        AppUser userById = userService.findUserById(1L);
        BaseRequestResponse baseRequestResponse = shareGroupService.addShareGroup(ramsarTrip, userById);

    }
}
