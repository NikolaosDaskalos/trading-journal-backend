package gr.aueb.cf3.tradingjournalapp.repository;

import gr.aueb.cf3.tradingjournalapp.model.Trade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    @Query("SELECT T FROM Trade T JOIN FETCH T.user U WHERE T.ticker = :ticker AND U.username = :username")
    List<Trade> findTradesByTickerStartingWith(String ticker, String username);

    @Query("SELECT T FROM Trade T JOIN FETCH T.user U WHERE U.username = :username")
    List<Trade> findAllTradesByUser(String username);

    @Query("SELECT T FROM Trade T JOIN FETCH T.user U WHERE T.id = :tradeId AND U.username = :username")
    Trade findTradeByIdForSpecificUser(Long tradeId, String username);
}
