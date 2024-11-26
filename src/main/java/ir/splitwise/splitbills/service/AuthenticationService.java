package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.entity.Role;
import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.AuthResponse;
import ir.splitwise.splitbills.models.LoginRequest;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterUserRequest request) throws DuplicateDataException {

        Optional<AppUser> foundUser = userRepository.findByEmail(request.email());
        if (foundUser.isPresent()) {
            throw new DuplicateDataException("this email is already exist");
        }

        AppUser appUser = AppUser.builder()
                .firstname(request.firstName()).lastName(request.lastname())
                .email(request.email()).password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(appUser);
        String token = jwtService.generateToken(appUser);//todo
        return new AuthResponse(token);

    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }//todo fix not generate more than n request in seconds and when get new expitre other token
}
