package group.project;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * StatisticManager (Business Logic Layer)
 *
 * Provides simple statistics based on event.csv.
 * All calculations are derived from Event.start_time/end_time (String, parsed with EventManager.TIME_FMT).
 */
public class StatisticManager {

    private final File_IO io;

    public StatisticManager(File_IO io) {
        this.io = io;
    }

    /**
     * Return the busiest weekday based on number of events' start dates.
     * If no valid events, returns null.
     */
    public DayOfWeek getBusiestDayInWeek() {
        ArrayList<Event> all = io.read_event();
        int[] cnt = new int[8]; // 1..7

        for (Event e : all) {
            LocalDateTime s = EventManager.parseTimeSafe(e.get_start_time());
            if (s == null) continue;
            int v = s.getDayOfWeek().getValue();
            cnt[v]++;
        }

        int bestV = -1, bestC = -1;
        for (int v = 1; v <= 7; v++) {
            if (cnt[v] > bestC) {
                bestC = cnt[v];
                bestV = v;
            }
        }
        return bestC <= 0 ? null : DayOfWeek.of(bestV);
    }

    /**
     * Distribution of events by category (case-sensitive as stored).
     * Empty/null category will be grouped under "(Uncategorized)".
     */
    public Map<String, Integer> getEventCategoryDistribution() {
        ArrayList<Event> all = io.read_event();
        Map<String, Integer> map = new HashMap<>();

        for (Event e : all) {
            String c = e.get_category();
            if (c == null || c.trim().isEmpty()) c = "(Uncategorized)";
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }

    /**
     * Count events that start in the given month (use the first day of that month).
     * Example input: LocalDate.of(2026, 1, 1)
     */
    public int getMonthlyEventCount(LocalDate month) {
        if (month == null) return 0;
        int y = month.getYear();
        int m = month.getMonthValue();

        ArrayList<Event> all = io.read_event();
        int count = 0;

        for (Event e : all) {
            LocalDateTime s = EventManager.parseTimeSafe(e.get_start_time());
            if (s == null) continue;
            if (s.getYear() == y && s.getMonthValue() == m) count++;
        }
        return count;
    }

    /**
     * Average event duration in minutes (only for events with valid start/end).
     * Returns 0.0 if no valid events.
     */
    public double getAverageEventDurationMinutes() {
        ArrayList<Event> all = io.read_event();
        long total = 0;
        int n = 0;

        for (Event e : all) {
            LocalDateTime s = EventManager.parseTimeSafe(e.get_start_time());
            LocalDateTime t = EventManager.parseTimeSafe(e.get_end_time());
            if (s == null || t == null) continue;
            if (!s.isBefore(t)) continue;

            long mins = java.time.Duration.between(s, t).toMinutes();
            total += mins;
            n++;
        }
        return n == 0 ? 0.0 : (double) total / n;
    }
}
