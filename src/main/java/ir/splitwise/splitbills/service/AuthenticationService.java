package ir.splitwise.splitbills.service;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.models.AuthResponse;
import ir.splitwise.splitbills.models.LoginRequest;
import ir.splitwise.splitbills.models.RegisterUserRequest;
import ir.splitwise.splitbills.models.enumeration.Role;
import ir.splitwise.splitbills.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String COOKIE_NAME = "X-Auth-Token";
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Long JWT_TIME_TOKEN = 1000 * 60L;

    public AuthResponse register(RegisterUserRequest request, HttpServletResponse response) throws DuplicateDataException {

        var appUser = getUser(request);
        appUser.setRole(Role.USER);
        userRepository.save(appUser);
        var token = jwtService.generateToken(appUser);
        setCookie(response, token, System.currentTimeMillis() + JWT_TIME_TOKEN);

        return new AuthResponse(token);
    }

    public void registerAdmin(RegisterUserRequest request) throws DuplicateDataException {
        AppUser appUser = getUser(request);
        appUser.setRole(Role.ADMIN);
        userRepository.save(appUser);
    }

    private AppUser getUser(RegisterUserRequest request) throws DuplicateDataException {
        var foundUser = userRepository.findByEmail(request.email());
        if (foundUser.isPresent()) {
            throw new DuplicateDataException("this email is already exist");
        }
        return buildAppUser(request);
    }

    private AppUser buildAppUser(RegisterUserRequest request) {//todo model Mapper
        return AppUser.builder()
                .firstname(request.firstName())
                .lastName(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }

    public void login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        var token = jwtService.generateToken(user);
        setCookie(response, token, System.currentTimeMillis() + JWT_TIME_TOKEN);
    }

    public void logout(HttpServletResponse response) {
        setCookie(response, "", 0);
    }

    private static void setCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
