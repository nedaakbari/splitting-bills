package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public AppUser findUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user " + id + "not found"));
    }

    public List<AppUser> findAllUserById(List<Long> ids) throws UserNotFoundException {
        List<AppUser> foundUsers = userRepository.findAllById(ids);
        if (!foundUsers.isEmpty() && foundUsers.size() == ids.size()) {
            return foundUsers;
        }
        log.info("not match user with ids :{} ", ids);
        throw new UserNotFoundException("user not found");
    }
}
