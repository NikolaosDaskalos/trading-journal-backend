package gr.aueb.cf3.tradingjournalapp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TRADES")
public class Trade {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "TICKER", nullable = false)
    private String ticker;

    @Column(name = "BUY_DATE", nullable = false)
    private LocalDate buyDate;

    @Column(name = "BUY_QUANTITY", nullable = false)
    private Integer buyQuantity;

    @Column(name = "BUY_PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal buyPrice;

    @Column(name = "POSITION", nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(name = "SELL_DATE")
    private LocalDate sellDate;

    @Column(name = "SELL_QUANTITY")
    private Integer sellQuantity;

    @Column(name = "SELL_PRICE", precision = 10, scale = 2)
    private BigDecimal sellPrice;

    @Column(name = "PROFIT_LOSS")
    @Setter(AccessLevel.NONE)
    private BigDecimal profitLoss;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    private User user;
}
