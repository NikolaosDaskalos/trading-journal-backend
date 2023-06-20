package gr.aueb.cf3.tradingjournalapp.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "STATISTICS")
public class Statistics {
    @Id
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "PROFIT")
    private BigDecimal profit;

    @Column(name = "GAINS_PER_DAY")
    private BigDecimal gainsPerDay;

    @Column(name = "WIN_RATE")
    private BigDecimal winRate;

    @Column(name = "OPEN_POSITIONS")
    private Long openPositions;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID")
    @Setter(AccessLevel.NONE)
    private User user;

    public void setUser(User user) {
        this.user = user;
        user.setStatistics(this);
    }


}
