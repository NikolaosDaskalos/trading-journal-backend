package gr.aueb.cf3.tradingjournalapp.repository;

import gr.aueb.cf3.tradingjournalapp.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Statistics findStatsById(Long id);
}
