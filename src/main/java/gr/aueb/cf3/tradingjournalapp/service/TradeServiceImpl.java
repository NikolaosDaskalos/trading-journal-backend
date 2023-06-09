package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Position;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeUserCorrelationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements ITradeService {
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    public List<Trade> findAllTradesByUser(String username) {
        log.info("Finding All Trades of user {}", username);
        return tradeRepository.findAllTradesByUser(username);
    }

    public List<Trade> findTradesByTicker(String ticker, String username) {
        log.info("Finding Trades of user {} with ticker {}", username, ticker);
        return tradeRepository.findTradesByTickerStartingWith(ticker, username);
    }

    public Trade findTradeById(Long id, String username) throws TradeNotFoundException {
        log.info("Finding Trade of user {} with id {}", username, id);

        Trade trade = tradeRepository.findTradeByIdForSpecificUser(id, username);

        if (trade == null) {
            log.warn("Trade with id: {} not found for user {}", id, username);
            throw new TradeNotFoundException(id);
        }

        return trade;
    }

    @Transactional
    public Trade createTrade(TradeDTO tradeDTO, String username) {
        log.info("Creating new Trade for user {}", username);
        Trade trade = mapToTrade(tradeDTO, username);
        trade.setId(null);
        return tradeRepository.save(trade);
    }

    @Transactional
    public Trade updateTrade(TradeDTO tradeDTO, String username) throws TradeNotFoundException, TradeUserCorrelationException {
        log.info("Updating Trade with id: {}", tradeDTO.getId());
        Optional<Trade> oldTrade = tradeRepository.findById(tradeDTO.getId());

        if (oldTrade.isEmpty()) {
            log.warn("Trade with id: {} can not be found update canceled", tradeDTO.getId());
            throw new TradeNotFoundException(tradeDTO.getId());
        } else if (!oldTrade.get().getUser().getUsername().equals(username)) {
            log.warn("Trade Update error trade with id {} don't belong to user {}", tradeDTO.getId(), username);
            throw new TradeUserCorrelationException(tradeDTO.getId(), username);
        }

        Trade trade = mapToTrade(tradeDTO, username);
        return tradeRepository.save(trade);

    }

    @Transactional
    public Trade deleteTrade(Long id, String username) throws TradeNotFoundException {
        log.info("Deleting trade with id {}", id);
        Trade trade = tradeRepository.findTradeByIdForSpecificUser(id, username);

        if (trade == null) {
            log.error("Trade delete error, trade with id: {} for user: {} don't exist", id, username);
            throw new TradeNotFoundException(id);
        }

        tradeRepository.deleteById(id);
        return trade;
    }

    private BigDecimal calculateProfitLoss(TradeDTO dto) {
        if (dto.getSellPrice() == null || dto.getSellQuantity() == null) {
            return null;
        }

        BigDecimal totalBuyAmount = dto.getBuyPrice().multiply(new BigDecimal(dto.getBuyQuantity()));
        BigDecimal totalSellAmount = dto.getSellPrice().multiply(new BigDecimal(dto.getSellQuantity()));
        return dto.getPosition().equalsIgnoreCase(Position.LONG.toString()) ?
                totalSellAmount.subtract(totalBuyAmount) :
                totalBuyAmount.subtract(totalSellAmount);
    }


    private Trade mapToTrade(TradeDTO dto, String username) {
        return Trade.builder()
                .id(dto.getId())
                .ticker(dto.getTicker())
                .buyDate(dto.getBuyDate())
                .buyQuantity(dto.getBuyQuantity())
                .buyPrice(dto.getBuyPrice())
                .position(mapPosition(dto.getPosition()))
                .sellDate(dto.getSellDate())
                .sellQuantity(dto.getSellQuantity())
                .sellPrice(dto.getSellPrice())
                .user(attachUser(username))
                .profitLoss(calculateProfitLoss(dto))
                .build();
    }

    private Position mapPosition(String position) {
        String formattedPosition = position.trim().toUpperCase();
        return Position.valueOf(formattedPosition);
    }

    private User attachUser(String username) {
        return userRepository.findUserByUsername(username);
    }

}
