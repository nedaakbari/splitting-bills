package ir.splitwise.splitbills.models;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(@NotBlank(message = "username can not be empty",groups = {AppUser.class}) String username,
                                  @NotBlank(message = "password can not be empty",groups = {AppUser.class}) String password,
                                  @NotBlank(message = "email can not be empty",groups = {AppUser.class}) String email) {
}
