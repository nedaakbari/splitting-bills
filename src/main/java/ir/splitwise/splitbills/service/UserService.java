package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<?> getAllGroupOfUser() {
        //todo
        return null;
    }

    public List<?> getAllActiveGroupOfUser() {
        //todo
        return null;
    }


}
