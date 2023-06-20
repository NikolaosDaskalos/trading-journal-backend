package gr.aueb.cf3.tradingjournalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @Positive(message = "must be positive number")
    private Long id;

    @NotNull(message = "must not be null")
    @NotBlank(message = "must not be empty string")
    private String name;

    @NotNull(message = "must not be null")
    @NotBlank(message = "must not be empty string")
    private String lastname;

    @NotNull(message = "must not be null")
    @Min(value = 18L, message = "Users must be ${value} or older")
    private Integer age;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 13, message = "Username must be between ${min} and ${max} characters")
    private String username;

    @NotNull(message = "must not be null")
    @Size(min = 8, message = "must have at least ${min} characters")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)(?=.*?[#?!@$%^&*-]).*$", message = "must contains at least one: Uppercase letter, Lowercase letter, digit, symbol")
    private String password;

    @NotNull(message = "must not be null")
    @Email(message = "wrong email format")
    private String email;
}