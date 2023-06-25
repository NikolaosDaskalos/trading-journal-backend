package gr.aueb.cf3.tradingjournalapp.service

import gr.aueb.cf3.tradingjournalapp.BaseTest
import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO
import gr.aueb.cf3.tradingjournalapp.model.Position
import gr.aueb.cf3.tradingjournalapp.model.Trade
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository
import spock.lang.Subject

import java.time.LocalDate

class TradeServiceTest extends BaseTest {

    TradeRepository tradeRepository = Mock(TradeRepository)
    UserRepository userRepository = Mock(UserRepository)

    User user = new User(id: 1L, firstname: 'John', lastname: 'Doe', age: 34, username: 'user01', password: 'p4ssWord', email: 'user02@aueb.com')

    TradeDTO tradeDTO = new TradeDTO(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            'SHORT', LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO)

    Trade trade = new Trade(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            Position.SHORT, LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO, user)

    @Subject
    ITradeService tradeService = new TradeServiceImpl(tradeRepository, userRepository)

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

    def "Happy Path - find all trades by user"() {
        when: 'find all trades by user is called'
        List<Trade> resultList = tradeService.findAllTradesByUser('user01')

        then: 'trades should match'
        1 * tradeRepository.findAllTradesByUser('user01') >> [trade, trade]

        and:'two lists should match'
        resultList == [trade,trade]
    }

//    def "Happy Path - find trades by ticker"() {
//        when: ''
//    }

    private assertTrade( Trade expectedTrade, Trade resultTrade) {
        expectedTrade.id == resultTrade.id
        expectedTrade.ticker == resultTrade.ticker
        expectedTrade.buyDate == resultTrade.buyDate
        expectedTrade.buyPrice == resultTrade.buyPrice
        expectedTrade.buyPrice == resultTrade.buyPrice
    }
}
