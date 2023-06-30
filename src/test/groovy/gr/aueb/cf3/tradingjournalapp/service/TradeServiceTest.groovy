package gr.aueb.cf3.tradingjournalapp.service

import ch.qos.logback.classic.Level
import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO
import gr.aueb.cf3.tradingjournalapp.model.Position
import gr.aueb.cf3.tradingjournalapp.model.Trade
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeNotFoundException
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeUserCorrelationException
import spock.lang.Subject

import java.time.LocalDate

class TradeServiceTest extends TestSpec {

    TradeRepository tradeRepository = Mock(TradeRepository)

    UserRepository userRepository = Mock(UserRepository)

    User user = new User(id: 1L, firstname: 'John', lastname: 'Doe', age: 34, username: 'user01', password: 'p4ssWord', email: 'user02@aueb.com')

    TradeDTO tradeDTO = new TradeDTO(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            'SHORT', LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO)

    Trade trade = new Trade(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            Position.SHORT, LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO, user)

    @Subject
    ITradeService tradeService = new TradeServiceImpl(tradeRepository, userRepository)

    def "Happy Path - find all trades by user"() {
        when: 'find all trades by user is called'
            List<Trade> resultList = tradeService.findAllTradesByUser('user01')

        then: 'trades should match'
            1 * tradeRepository.findAllTradesByUser('user01') >> [trade, trade]
            0 * _

        and: 'two lists should match'
            resultList == [trade, trade]
    }

    def "Happy Path - find trades by ticker"() {
        when: 'find all trades by ticker is called'
            List<Trade> resultList = tradeService.findTradesByTicker('AU', 'user01')

        then: 'trades should match'
            1 * tradeRepository.findTradesByTickerStartingWith('AU', 'user01') >> [trade, trade]
            0 * _

        and: 'two lists should match'
            resultList == [trade, trade]
    }

    def "Happy Path - find trade by id"() {
        when: 'find trade by id is invoked'
            Trade resultTrade = tradeService.findTradeById(1L, 'user01')

        then: 'tradeRepository finds the trade'
            1 * tradeRepository.findTradeByIdForSpecificUser(1L, 'user01') >> trade
            0 * _

        and: 'two lists should match'
            resultTrade == trade
    }

    def "Unhappy Path - find trade by id throws TradeNotFoundException"() {
        when: 'find trade by id is invoked'
            tradeService.findTradeById(1L, 'user01')

        then: 'tradeRepository does not find the trade'
            1 * tradeRepository.findTradeByIdForSpecificUser(1L, 'user01') >> null
            0 * _

        and: 'two lists should match'
            def e = thrown(TradeNotFoundException)
            e.message == "Trade with id: 1 did not exist"
            assertLog(Level.WARN, "Trade with id: 1 not found for user user01")
    }

    def "Happy Path - create a new Trade"() {
        when: "a new trade is created"
            Trade resultTrade = tradeService.createTrade(tradeDTO, 'user01')

        then: "the userRepo and tradeRepo is called"
            1 * userRepository.findUserByUsername('user01') >> user
            1 * tradeRepository.save(_ as Trade) >> trade
            0 * _

        and: "the result trade should match"
            trade == resultTrade
    }

    def "Happy Path - update Trade"() {
        given: 'we have a trade with the same id in our db'
            Trade oldTrade = new Trade(1L, 'OLD', LocalDate.now().minusDays(2), 2, new BigDecimal('32.89'),
                    Position.SHORT, LocalDate.now().minusDays(1), 2, new BigDecimal('87.09'), BigDecimal.ZERO, user)

        when: "an existing trade is updated"
            Trade resultTrade = tradeService.updateTrade(tradeDTO, 'user01')

        then: "the userRepo and tradeRepo is called"
            1 * tradeRepository.findById(tradeDTO.id) >> Optional.of(oldTrade)
            1 * userRepository.findUserByUsername('user01') >> user
            1 * tradeRepository.save(_ as Trade) >> trade
            0 * _

        and: "the result trade should match"
            trade == resultTrade
    }

    def "Unhappy Path - update Trade throws TradeNotFoundException"() {
        when: "an existing trade is updated"
            tradeService.updateTrade(tradeDTO, 'user01')

        then: "the trade repository returns null "
            1 * tradeRepository.findById(tradeDTO.id) >> Optional.ofNullable(null)

        and: "exception is thrown and not other method is called"
            assertLog(Level.WARN, "Trade with id: $tradeDTO.id can not be found update canceled")
            def e = thrown(TradeNotFoundException)
            e.message == "Trade with id: $tradeDTO.id did not exist"
            0 * _
    }

    def "Unhappy Path - update Trade throws TradeUserCorrelationException"() {
        given: 'we have a trade with the same id but different user in our db'
            Trade oldTrade = new Trade(1L, 'OLD', LocalDate.now().minusDays(2), 2, new BigDecimal('32.89'),
                    Position.SHORT, LocalDate.now().minusDays(1), 2, new BigDecimal('87.09'), BigDecimal.ZERO, new User(username: 'user02'))

        when: "an existing trade is updated"
            tradeService.updateTrade(tradeDTO, 'user01')

        then: "the trade repository returns null "
            1 * tradeRepository.findById(tradeDTO.id) >> Optional.ofNullable(oldTrade)

        and: "exception is thrown and not other method is called"
            assertLog(Level.WARN, "Trade Update error trade with id $tradeDTO.id don't belong to user user01")
            def e = thrown(TradeUserCorrelationException)
            e.message == "Trade with id $tradeDTO.id does not belong to user user01"
            0 * _
    }

    def "Happy Path - delete Trade"() {
        when: "an existing trade is deleted"
            Trade resultTrade = tradeService.deleteTrade(1L, 'user01')

        then: "the trade repository returns the trade"
            1 * tradeRepository.findTradeByIdForSpecificUser(1L, 'user01') >> trade

        and: "repository delete method is invoked"
            1 * tradeRepository.deleteById(1L)
            resultTrade == trade
            0 * _
    }

    def "Unhappy Path - delete trade throws TradeNotFoundException"() {
        when: "trying to delete a trade"
            tradeService.deleteTrade(1L, 'user01')

        then: "the trade repository returns null "
            1 * tradeRepository.findTradeByIdForSpecificUser(1L, 'user01') >> null

        and: "exception is thrown and not other method is called"
            assertLog(Level.ERROR, "Trade delete error, trade with id: 1 for user: user01 don't exist")
            def e = thrown(TradeNotFoundException)
            e.message == "Trade with id: 1 did not exist"
            0 * _
    }


}
