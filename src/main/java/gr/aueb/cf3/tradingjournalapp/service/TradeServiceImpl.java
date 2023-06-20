package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.model.Trade.Position;
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeUserCorrelationException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TradeServiceImpl implements ITradeService {
    private static final Logger log = LoggerFactory.getLogger(TradeServiceImpl.class);
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    public Trade findTradeById(Long id, String username) throws TradeNotFoundException {
        log.info("Finding Trade with id {}", id);
        Trade trade = this.tradeRepository.findTradeByIdForSpecificUser(id, username);
        if (trade == null) {
            log.warn("Trade with id: {} not found for user {}", id, username);
            throw new TradeNotFoundException(id);
        } else {
            return trade;
        }
    }

    public List<Trade> findTradesByTicker(String ticker, String username) {
        log.info("Finding Trades with ticker name {}", ticker);
        List<Trade> trades = this.tradeRepository.findTradesByTickerStartingWith(ticker, username);
        if (CollectionUtils.isEmpty(trades)) {
            log.warn("Trades with Ticker: {} not found for user {}", ticker, username);
        }

        return trades;
    }

    public List<Trade> findAllTradesByUser(String username) {
        log.info("Finding All Trades");
        return this.tradeRepository.findAllTradesByUser(username);
    }

    @Transactional
    public Trade createTrade(TradeDTO tradeDTO, String username) {
        log.info("Creating new Trade");
        Trade trade = this.mapToTrade(tradeDTO, username);
        trade.setId((Long)null);
        return (Trade)this.tradeRepository.save(trade);
    }

    @Transactional
    public Trade updateTrade(TradeDTO tradeDTO, String username) throws TradeNotFoundException, TradeUserCorrelationException {
        log.info("Updating Trade with id: {}", tradeDTO.getId());
        Optional<Trade> oldTrade = this.tradeRepository.findById(tradeDTO.getId());
        if (oldTrade.isEmpty()) {
            log.warn("Trade with id: {} can not be found update canceled", tradeDTO.getId());
            throw new TradeNotFoundException(tradeDTO.getId());
        } else if (!((Trade)oldTrade.get()).getUser().getUsername().equals(username)) {
            log.error("Trade Update error trade with id {} don't belong to user {}", tradeDTO.getId(), username);
            throw new TradeUserCorrelationException(tradeDTO.getId(), username);
        } else {
            Trade trade = this.mapToTrade(tradeDTO, username);
            return (Trade)this.tradeRepository.save(trade);
        }
    }

    @Transactional
    public Trade deleteTrade(Long id, String username) throws TradeNotFoundException {
        log.info("Deleting trade with id {}", id);
        Trade trade = this.tradeRepository.findTradeByIdForSpecificUser(id, username);
        if (trade == null) {
            log.error("Trade delete error, trade with id: {} for user: {} don't exist", id, username);
            throw new TradeNotFoundException(id);
        } else {
            this.tradeRepository.deleteById(id);
            return trade;
        }
    }

    private Trade mapToTrade(TradeDTO dto, String username) {
        return Trade.builder().id(dto.getId()).companyName(dto.getCompanyName()).ticker(dto.getTicker()).buyDate(dto.getBuyDate()).buyQuantity(dto.getBuyQuantity()).buyPrice(dto.getBuyPrice()).position(this.mapPosition(dto.getPosition())).sellDate(dto.getSellDate()).sellQuantity(dto.getSellQuantity()).sellPrice(dto.getSellPrice()).user(this.attachUser(username)).build();
    }

    private Trade.Position mapPosition(String position) {
        String formattedPosition = position.trim().toUpperCase();
        return Position.valueOf(formattedPosition);
    }

    private User attachUser(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    public TradeServiceImpl(final TradeRepository tradeRepository, final UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
    }
}
