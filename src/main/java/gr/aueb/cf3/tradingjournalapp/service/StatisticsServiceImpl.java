package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.model.Position;
import gr.aueb.cf3.tradingjournalapp.model.Statistics;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.StatisticsRepository;
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {
    private final StatisticsRepository statsRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Statistics calculateUserStats(String username) {
        User user = userRepository.findUserByUsername(username);
        List<Trade> userTrades = tradeRepository.findAllTradesByUser(username);
        Statistics stats = statsRepository.findStatsById(user.getId());

        if (stats == null) {
           stats = statsRepository.save(initializeStatistics(user));
        }

        if (userTrades.isEmpty()) {
            return stats;
        }

        stats.setProfit(calculateProfit(userTrades));
        stats.setWinRate(calculateWinningRate(userTrades));
        stats.setOpenPositions(countOpenPositions(userTrades));
        stats.setGainsPerDay(calculateGainsPerDay(userTrades));
        return statsRepository.save(stats);

    }

    private Statistics initializeStatistics(User user) {
        return Statistics.builder()
                .gainsPerDay(BigDecimal.ZERO)
                .winRate(BigDecimal.ZERO)
                .profit(BigDecimal.ZERO)
                .openPositions(0L)
                .user(user)
                .build();
    }

    private BigDecimal calculateGainsPerDay(Collection<Trade> trades) {
        List<LocalDate> allDates = trades.stream()
                .flatMap(trade -> Stream.of(trade.getBuyDate(), trade.getSellDate()))
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        long totalDays = ChronoUnit.DAYS.between(allDates.get(0), allDates.get(allDates.size() - 1));

        return totalDays != 0 ? calculateProfit(trades).divide(new BigDecimal(totalDays), 2, RoundingMode.HALF_EVEN) : calculateProfit(trades);
    }

    private long countOpenPositions(Collection<Trade> trades) {
        return trades.stream()
                .filter(trade -> trade.getSellPrice() == null)
                .count();
    }

    private BigDecimal calculateProfit(Collection<Trade> trades) {
        return trades.stream()
                .filter(trade -> trade.getSellPrice() != null)
                .map(trade -> {
                    BigDecimal buyTotalAmount = trade.getBuyPrice().multiply(new BigDecimal(trade.getBuyQuantity()));
                    BigDecimal sellTotalAmount = trade.getSellPrice().multiply(new BigDecimal(trade.getSellQuantity()));

                    if (trade.getPosition() == Position.LONG) {
                        return sellTotalAmount.subtract(buyTotalAmount);
                    } else {
                        return buyTotalAmount.subtract(sellTotalAmount);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateWinningRate(Collection<Trade> trades) {

        long winningTrades = trades.stream()
                .filter(trade -> {
                    if (trade.getSellPrice() == null) {
                        return false;
                    }
                    BigDecimal buyTotalAmount = trade.getBuyPrice().multiply(new BigDecimal(trade.getBuyQuantity()));
                    BigDecimal sellTotalAmount = trade.getSellPrice().multiply(new BigDecimal(trade.getSellQuantity()));
                    if (trade.getPosition() == Position.LONG && sellTotalAmount.compareTo(buyTotalAmount) > 0) {
                        return true;
                    } else {
                        return trade.getPosition() == Position.SHORT && buyTotalAmount.compareTo(sellTotalAmount) > 0;
                    }
                })
                .count();

        long allClosedTrades = trades.stream()
                .filter(trade -> trade.getSellPrice() != null)
                .count();


        return allClosedTrades != 0 ?
                new BigDecimal(winningTrades)
                        .divide(new BigDecimal(allClosedTrades), 4, RoundingMode.HALF_EVEN)
                        .multiply(new BigDecimal(100)) :
                BigDecimal.ZERO;

    }
}
