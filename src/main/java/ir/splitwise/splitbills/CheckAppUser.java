package ir.splitwise.splitbills;

import ir.splitwise.splitbills.entity.AppUser;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckAppUser {
    public static AppUser checkUserInstance(Authentication authentication) throws UserNotFoundException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUser appUser) {
            return appUser;
        }
        throw new UserNotFoundException("");
    }
}
