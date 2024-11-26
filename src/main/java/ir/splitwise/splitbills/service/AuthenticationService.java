package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Role;
import ir.splitwise.splitbills.models.RegisterResponse;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public RegisterResponse register(RegisterUserRequest request) {
        AppUser appUser = AppUser.builder()
                .firstname(request.firstName()).lastName(request.lastname())
                .email(request.email()).password(request.password())//todo
                .role(Role.USER)
                .build();
        userRepository.save(appUser);
        String token = jwtService.generateToken(appUser);//todo
        return new RegisterResponse(token);

    }
}
