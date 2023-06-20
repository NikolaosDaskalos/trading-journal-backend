
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
    @Positive
    private Long id;

    private String companyName;

    @NotBlank
    @Size(min = 2, max = 5, message = "{ticker.size}")
    private String ticker;

    @NotNull
    @PastOrPresent
    private LocalDate buyDate;

    @NotNull
    @Positive
    private Integer buyQuantity;

    @NotNull
    @Positive
    private BigDecimal buyPrice;

    @NotBlank
    @Pattern(regexp = "(?i)(SHORT|LONG)")
    private String position;

    @PastOrPresent
    private LocalDate sellDate;

    @Positive
    private Integer sellQuantity;

    @Positive
    private BigDecimal sellPrice;
}
