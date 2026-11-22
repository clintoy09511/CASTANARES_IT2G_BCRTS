package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;
import java.util.List;

public class ActivityLogViewer {
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public void displayLogs() {
        List<ActivityLog> logs = activityLogDao.getAllLogs();
        System.out.println("\n=== ACTIVITY LOGS ===");
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }
        for (ActivityLog log : logs) {
            System.out.printf("[%s] User #%d | %s - %s%n",
                    log.getTimestamp(), log.getUserId(), log.getAction(), log.getDescription());
        }
    }
}