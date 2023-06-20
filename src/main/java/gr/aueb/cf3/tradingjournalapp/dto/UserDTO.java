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
    @Positive
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String lastname;

    @NotNull
    @Min(value = 18L, message = "Users must be ${value} or older")
    private Integer age;

    @NotNull
    @Size(min = 5, max = 13, message = "Username must be between ${min} and ${max} characters")
    private String username;

    @NotNull
    @Size(min = 8, message = "Password must have at least ${min} characters")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)(?=.*?[#?!@$%^&*-]).*$", message = "Password must contains at least one: Uppercase letter, Lowercase letter, digit, symbol")
    private String password;

    @NotNull
    @Email
    private String email;
}