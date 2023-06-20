package gr.aueb.cf3.tradingjournalapp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatsDTO {
    private Long id;
    private BigDecimal profit;
    private BigDecimal gainsPerDay;
    private BigDecimal winRate;
    private Long openPositions;
}
