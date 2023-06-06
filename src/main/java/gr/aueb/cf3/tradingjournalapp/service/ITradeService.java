package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeUserCorrelationException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import java.util.List;

public interface ITradeService {
    Trade findTradeById(Long id, String username) throws TradeNotFoundException;

    List<Trade> findTradesByTicker(String ticker, String username);

    List<Trade> findAllTradesByUser(String username);

    Trade createTrade(TradeDTO tradeDTO, String username) throws UserNotFoundException;

    Trade updateTrade(TradeDTO tradeDTO, String username) throws TradeNotFoundException, TradeUserCorrelationException;

    Trade deleteTrade(Long id, String username) throws TradeNotFoundException;
}
