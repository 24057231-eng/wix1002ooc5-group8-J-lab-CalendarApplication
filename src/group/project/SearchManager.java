package group.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchManager (Business Logic Layer)
 *
 * Provides search and filter utilities based on the data stored in event.csv.
 * This class uses File_IO.read_event() to get the current snapshot of events.
 */
public class SearchManager {

    private final File_IO io;

    public SearchManager(File_IO io) {
        this.io = io;
    }

    /**
     * Search events by exact date (any event whose start_time date equals targetDate).
     */
    public List<Event> searchEventsByDate(LocalDate targetDate) {
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            LocalDateTime s = EventManager.parseTimeSafe(e.get_start_time());
            if (s == null) continue;
            if (s.toLocalDate().equals(targetDate)) res.add(e);
        }
        return res;
    }

    /**
     * Search events within a date range (inclusive).
     * Rule: event overlaps the date window [start, end] if:
     * eventEndDate >= start AND eventStartDate <= end
     */
    public List<Event> searchEventsByDateRange(LocalDate start, LocalDate end) {
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            LocalDateTime s = EventManager.parseTimeSafe(e.get_start_time());
            LocalDateTime t = EventManager.parseTimeSafe(e.get_end_time());
            if (s == null || t == null) continue;

            LocalDate sd = s.toLocalDate();
            LocalDate ed = t.toLocalDate();

            boolean overlap = (ed.isAfter(start) || ed.isEqual(start)) && (sd.isBefore(end) || sd.isEqual(end));
            if (overlap) res.add(e);
        }
        return res;
    }

    /**
     * Search events by title keyword (case-insensitive).
     */
    public List<Event> searchEventsByTitle(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            String title = e.get_title() == null ? "" : e.get_title().toLowerCase();
            if (title.contains(k)) res.add(e);
        }
        return res;
    }

    public List<Event> filterEventsByCategory(String category) {
        String c = category == null ? "" : category.trim().toLowerCase();
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            String v = e.get_category() == null ? "" : e.get_category().trim().toLowerCase();
            if (v.equals(c)) res.add(e);
        }
        return res;
    }

    public List<Event> filterEventsByLocation(String location) {
        String c = location == null ? "" : location.trim().toLowerCase();
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            String v = e.get_location() == null ? "" : e.get_location().trim().toLowerCase();
            if (v.equals(c)) res.add(e);
        }
        return res;
    }

    /**
     * Filter events that contain a specific attendee name (case-insensitive exact match).
     */
    public List<Event> filterEventsByAttendee(String attendeeName) {
        String k = attendeeName == null ? "" : attendeeName.trim().toLowerCase();
        ArrayList<Event> all = io.read_event();
        ArrayList<Event> res = new ArrayList<>();

        for (Event e : all) {
            boolean hit = false;
            for (String a : e.get_attendees()) {
                if (a == null) continue;
                if (a.trim().toLowerCase().equals(k)) {
                    hit = true;
                    break;
                }
            }
            if (hit) res.add(e);
        }
        return res;
    }
}
