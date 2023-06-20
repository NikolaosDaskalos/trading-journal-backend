package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.StatsDTO;
import gr.aueb.cf3.tradingjournalapp.model.Statistics;
import gr.aueb.cf3.tradingjournalapp.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStatistics(Principal principal) {
        Statistics stats = statsService.calculateUserStats(principal.getName());
        return ResponseEntity.ok(mapToDTO(stats));
    }

    private StatsDTO mapToDTO(Statistics statistics) {
        return StatsDTO.builder()
                .profit(statistics.getProfit())
                .winRate(statistics.getWinRate())
                .openPositions(statistics.getOpenPositions())
                .gainsPerDay(statistics.getGainsPerDay())
                .build();
    }
}
