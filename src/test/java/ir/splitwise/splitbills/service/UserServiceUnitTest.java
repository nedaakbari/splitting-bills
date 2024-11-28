package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    private AppUser user;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        user = new AppUser();
        user.setId(1L);
        user.setEmail("neda@gmail.com");
    }
    @Test
    void findUserById_thenSuccess() throws UserNotFoundException {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        AppUser foundUser = userService.findUserById(1L);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(1L,foundUser.getId()); // :)
    }

}
