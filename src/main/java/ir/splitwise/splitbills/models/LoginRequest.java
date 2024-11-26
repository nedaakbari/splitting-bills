package ir.splitwise.splitbills.models;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "email can not be empty")String email,
                           @NotBlank(message = "password can not be empty")String password) {
}
