package gr.aueb.cf3.tradingjournalapp.service

import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.model.Position
import gr.aueb.cf3.tradingjournalapp.model.Role
import gr.aueb.cf3.tradingjournalapp.model.Statistics
import gr.aueb.cf3.tradingjournalapp.model.Token
import gr.aueb.cf3.tradingjournalapp.model.Trade
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.repository.StatisticsRepository
import gr.aueb.cf3.tradingjournalapp.repository.TradeRepository
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository
import spock.lang.Subject

import java.time.LocalDate


class StatisticsServiceTest extends TestSpec {

    private StatisticsRepository statsRepository = Mock(StatisticsRepository)
    private TradeRepository tradeRepository = Mock(TradeRepository)
    private UserRepository userRepository = Mock(UserRepository)

    User user = new User(1L, 'John', 'Doe', 34, 'user01', 'p4ssWord', 'user01@aueb.com', [new Trade(id: 3L)], Role.USER, [new Token(id: 4L, token: "gerwfge632vgh43")], new Statistics(id: 5L, profit: new BigDecimal(34.09)))

    Statistics randomStats = new Statistics(1L, new BigDecimal('45.66'), new BigDecimal('2.98'), new BigDecimal('45.4843'), 3L, user)

    Trade trade1 = new Trade(1L, 'AUEB', LocalDate.now(), 1, new BigDecimal('20.00'),
            Position.LONG, LocalDate.now(), 1, new BigDecimal('50.00'), BigDecimal.ZERO, user)

    Trade trade2 = new Trade(2L, 'APPL', LocalDate.now(), 1, new BigDecimal('90.00'),
            Position.SHORT, LocalDate.now(), 1, new BigDecimal('40.00'), BigDecimal.ZERO, user)

    Statistics stats = new Statistics(1L, new BigDecimal('80.00'), new BigDecimal('80.00'), new BigDecimal('100.0000'), 0L, user)

    @Subject
    IStatisticsService statsService = new StatisticsServiceImpl(statsRepository, tradeRepository, userRepository)

    def "Happy path - Calculate statistics when totalDays = 0"() {
        when: 'calculateUserStats is invoked'
            Statistics resultStats = statsService.calculateUserStats(user.username)

        then: 'trades found and statistics are calculated with totalDays = 0'
            1 * userRepository.findUserByUsername(user.username) >> user
            1 * tradeRepository.findAllTradesByUser(user.username) >> [trade1, trade2]
            1 * statsRepository.findStatsById(user.id) >> randomStats
            1 * statsRepository.save(stats) >> stats
            0 * _

        and: 'resulting statistics equals expected statistics'
            resultStats == stats
    }

    def "Happy path - Calculate statistics when totalDays is not 0"() {
        given: 'the trades dates resulting to more than 0 days'
            Trade trade3 = new Trade(3L, 'IBM', LocalDate.now().minusDays(2), 1, new BigDecimal('70.00'),
                    Position.LONG, LocalDate.now(), 1, new BigDecimal('50.00'), BigDecimal.ZERO, user)

            Trade openTrade = new Trade(id: 4L, ticker:'MSFT')
            Statistics expectedStats = new Statistics(1L, new BigDecimal('60.00'), new BigDecimal('30.00'), new BigDecimal('66.6700'), 1L, user)

        when: 'calculateUserStats is invoked'
            Statistics resultStats = statsService.calculateUserStats(user.username)

        then: 'trades found and statistics are calculated with totalDays = 0'
            1 * userRepository.findUserByUsername(user.username) >> user
            1 * tradeRepository.findAllTradesByUser(user.username) >> [trade1, trade2, trade3, openTrade]
            1 * statsRepository.findStatsById(user.id) >> randomStats
            1 * statsRepository.save(expectedStats) >> expectedStats
            0 * _

        and: 'resulting statistics equals expected statistics'
            resultStats == expectedStats
    }

    def "Happy path - Calculate statistics no trades found and stats == null"() {
        given: 'the statistics has not been initialized yet'
            Statistics zeroStats = new Statistics(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, user)

        when: 'calculateUserStats is invoked and the statistics have not been initialized for existing user'
            Statistics resultStats = statsService.calculateUserStats(user.username)

        then: 'two trades found and statistics are calculated with totalDays = 0'
            1 * userRepository.findUserByUsername(user.username) >> user
            1 * tradeRepository.findAllTradesByUser(user.username) >> []
            1 * statsRepository.findStatsById(user.id) >> null
            1 * statsRepository.save(zeroStats) >> zeroStats
            0 * _

        and: 'resulting statistics equals expected statistics'
            resultStats == zeroStats
    }

    def "Happy path - Calculate statistics no trades found but stats has been initialized"() {
        when: 'calculateUserStats is invoked and the statistics have been initialized'
            Statistics resultStats = statsService.calculateUserStats(user.username)

        then: 'no trades found but statistics have been initialized'
            1 * userRepository.findUserByUsername(user.username) >> user
            1 * tradeRepository.findAllTradesByUser(user.username) >> []
            1 * statsRepository.findStatsById(user.id) >> stats
            0 * _

        and: 'resulting statistics equals expected statistics'
            resultStats == stats
    }
}
