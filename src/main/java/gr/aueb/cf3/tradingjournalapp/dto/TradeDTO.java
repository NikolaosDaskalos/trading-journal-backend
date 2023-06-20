
package gr.aueb.cf3.tradingjournalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeDTO {
    @Positive(message = "must be positive number")
    private Long id;

    private String companyName;

    @NotNull(message = "must not be null")
    @NotBlank(message = "must have a value")
    @Size(min = 2, max = 5, message = "{ticker.size}")
    private String ticker;

    @NotNull(message = "must not be null")
    @PastOrPresent(message = "can not be future date")
    private LocalDate buyDate;

    @NotNull(message = "must not be null")
    @Positive(message = "must be positive number")
    private Integer buyQuantity;

    @NotNull(message = "must not be null")
    @Positive(message = "must be positive number")
    private BigDecimal buyPrice;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "(?i)(SHORT|LONG)", message = "should have value 'short' or 'long'")
    private String position;

    @PastOrPresent(message = "can not be future date")
    private LocalDate sellDate;

    @Positive(message = "must be positive number")
    private Integer sellQuantity;

    @Positive(message = "must be positive number")
    private BigDecimal sellPrice;
}
