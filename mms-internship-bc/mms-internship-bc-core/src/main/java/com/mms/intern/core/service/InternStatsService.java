package com.mms.intern.core.service;

import com.mms.intern.common.api.stats.StatsResponses;

public interface InternStatsService {

    StatsResponses.BatchStatistics batchStats(Long batchId);

    StatsResponses.Overview overview(Long batchId);
}
