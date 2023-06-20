package gr.aueb.cf3.tradingjournalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotNull(message = "${username.null}")
    @Size(min = 5, max = 13, message = "${username.size}")
    private String username;

    @NotNull
    @Size(min = 8, message = "Password must have at least ${min} characters")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)(?=.*?[#?!@$%^&*-]).*$", message = "Password must contain at least one: Uppercase letter, Lowercase letter, digit, symbol")
    private String password;
}
