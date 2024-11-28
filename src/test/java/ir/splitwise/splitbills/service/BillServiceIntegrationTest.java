package ir.splitwise.splitbills.service;

import com.google.gson.Gson;
import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.repository.BillRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BillServiceIntegrationTest {
    @Autowired
    BillService billService;
    @Autowired
    BillRepository billRepository;
    @Autowired
    UserService userService;
    @Autowired
    ShareGroupService shareGroupService;
    @Autowired
    Gson gson;
    private AppUser appUser1;
    private AppUser appUser2;
    private AppUser appUser3;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findUserById_theSuccess() throws UserNotFoundException {
        AppUser foundUser = userService.findUserById(appUser1.getId());
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(appUser1.getId(), foundUser.getId());
        Assertions.assertEquals(appUser1.getEmail(), foundUser.getEmail());
    }

    @Test
    void findUserById_whenUserUserNotFound_thenThroeException() {
        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(100));
    }

    @Test
    void findAllUserById_theSuccess() throws UserNotFoundException {
        List<Long> userIds = List.of(appUser1.getId(), appUser2.getId(), appUser3.getId());
        List<AppUser> foundUsers = userService.findAllUserById(userIds);
        Assertions.assertNotNull(foundUsers);
        Assertions.assertEquals(userIds.size(), foundUsers.size());
    }

    @Test
    void findUserById_whenSomeIdNotExist_theFailed() {
        List<Long> userIds = List.of(appUser1.getId(), appUser2.getId(), 100L);
        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.findAllUserById(userIds));
    }
}
