package group.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RecurrentEventUtils
 *
 * Helper to generate repeated Event instances from a base event + recurrence rule.
 * This does NOT automatically persist recurrent events. Your EventManager (or UI) can decide:
 * - Generate -> review -> save each occurrence
 * - Or save recurrence rule into recurrent.csv using File_IO.save_recurrent(...)
 */
public class RecurrentEventUtils {

    /**
     * Generate repeated events based on the rule.
     * Supported intervals:
     * - "1d" daily
     * - "1w" weekly
     * - "1m" monthly (same day-of-month, if possible)
     */
    public static List<Event> generateRecurrentEvents(Event base, RecurrentEvent rule) {
        ArrayList<Event> res = new ArrayList<>();
        if (base == null || rule == null) return res;

        LocalDateTime s = EventManager.parseTimeSafe(base.get_start_time());
        LocalDateTime t = EventManager.parseTimeSafe(base.get_end_time());
        if (s == null || t == null) return res;

        int times = rule.getRecurrentTimes();
        if (times <= 0) return res;

        String interval = rule.getRecurrentInterval() == null ? "" : rule.getRecurrentInterval().trim().toLowerCase();

        LocalDateTime curS = s;
        LocalDateTime curT = t;

        for (int i = 1; i <= times; i++) {
            curS = step(curS, interval);
            curT = step(curT, interval);

            Event e = new Event();
            // ID should be assigned by EventManager/File_IO.get_ID() at save time.
            e.set_title(base.get_title());
            e.set_description(base.get_description());
            e.set_start_time(curS.format(EventManager.TIME_FMT));
            e.set_end_time(curT.format(EventManager.TIME_FMT));
            e.set_location(base.get_location());
            e.set_category(base.get_category());
            e.set_attendees(base.get_attendees());

            res.add(e);
        }
        return res;
    }

    private static LocalDateTime step(LocalDateTime dt, String interval) {
        switch (interval) {
            case "1d": return dt.plusDays(1);
            case "1w": return dt.plusWeeks(1);
            case "1m": return dt.plusMonths(1);
            default:   return dt.plusDays(1); // fallback to daily
        }
    }
}
