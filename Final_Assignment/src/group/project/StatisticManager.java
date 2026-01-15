package group.project;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class StatisticManager {
    private final EventManager eventManager;

    public StatisticManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public DayOfWeek getBusiestDayInWeek() {
        Map<DayOfWeek, Integer> cnt = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) cnt.put(d, 0);

        for (Event e : eventManager.getAllEventsExpanded()) {
            if (e.getStartDateTimeAsLdt() == null) continue;
            DayOfWeek d = e.getStartDateTimeAsLdt().getDayOfWeek();
            cnt.put(d, cnt.get(d) + 1);
        }

        DayOfWeek best = DayOfWeek.MONDAY;
        int bestN = -1;
        for (DayOfWeek d : DayOfWeek.values()) {
            int n = cnt.get(d);
            if (n > bestN) { bestN = n; best = d; }
        }
        return best;
    }

    public Map<String, Integer> getEventCategoryDistribution() {
        Map<String, Integer> map = new HashMap<>();
        for (Event e : eventManager.getAllEventsExpanded()) {
            String c = (e.getCategory() == null || e.getCategory().trim().isEmpty()) ? "Uncategorized" : e.getCategory().trim();
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }

    public int getMonthlyEventCount(LocalDate month) {
        if (month == null) return 0;
        int y = month.getYear();
        int m = month.getMonthValue();

        int count = 0;
        for (Event e : eventManager.getAllEventsExpanded()) {
            if (e.getStartDateTimeAsLdt() == null) continue;
            if (e.getStartDateTimeAsLdt().getYear() == y && e.getStartDateTimeAsLdt().getMonthValue() == m) {
                count++;
            }
        }
        return count;
    }

    public double getAverageEventDuration() {
        long totalMinutes = 0;
        int n = 0;

        for (Event e : eventManager.getAllEventsExpanded()) {
            if (e.getStartDateTimeAsLdt() == null || e.getEndDateTimeAsLdt() == null) continue;
            long mins = Duration.between(e.getStartDateTimeAsLdt(), e.getEndDateTimeAsLdt()).toMinutes();
            if (mins > 0) {
                totalMinutes += mins;
                n++;
            }
        }
        return (n == 0) ? 0.0 : (double) totalMinutes / n;
    }
}