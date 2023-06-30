package gr.aueb.cf3.tradingjournalapp.controller

import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.dto.StatsDTO
import gr.aueb.cf3.tradingjournalapp.model.Statistics
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.service.IStatisticsService
import gr.aueb.cf3.tradingjournalapp.service.StatisticsServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Subject

import java.security.Principal

class StatisticsControllerTest extends TestSpec {

    IStatisticsService statisticsService = Mock(StatisticsServiceImpl)
    Principal principal = Mock(Principal)

    @Subject
    StatisticsController statisticsController = new StatisticsController(statisticsService)

    StatsDTO statsDTO = new StatsDTO(1L, new BigDecimal(22.10), new BigDecimal(5.98), new BigDecimal(60.89), 3L)
    Statistics stats = new Statistics(1L, new BigDecimal(22.10), new BigDecimal(5.98), new BigDecimal(60.89), 3L, new User(id: 1L, username: 'user01'))

    def "Happy Path - getStatistics is called"() {
        when: "getStatistics is invoked"
            ResponseEntity<StatsDTO> response = statisticsController.getStatistics(principal)

        then: "register service is called"
            1 * principal.getName() >> 'user01'
            1 * statisticsService.calculateUserStats('user01') >> stats
            response.statusCode == HttpStatus.OK
            response.body == statsDTO
            0 * _
    }
}
