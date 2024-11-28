package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private AppUser appUser1;
    private AppUser appUser2;
    private AppUser appUser3;

    @BeforeEach
    void setUp() {
        appUser1 = AppUser.builder()
                .email("test1@gmail.com")
                .build();
        appUser2 = AppUser.builder()
                .email("test2@gmail.com")
                .build();
        appUser3 = AppUser.builder()
                .email("test3@gmail.com")
                .build();

        userRepository.save(appUser1);
        userRepository.save(appUser2);
        userRepository.save(appUser3);
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
