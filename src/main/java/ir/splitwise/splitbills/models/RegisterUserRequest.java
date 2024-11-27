package ir.splitwise.splitbills.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterUserRequest(String firstName, String lastname,
                                  @NotBlank(message = "password can not be empty") String password,
                                  @NotBlank(message = "email can not be empty") @Email String email) {
}
