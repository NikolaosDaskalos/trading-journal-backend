package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.model.Statistics;

public interface IStatisticsService {
    Statistics calculateUserStats(String username);
}
