package group.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchManager {
    private final EventManager eventManager;

    public SearchManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public List<Event> searchEventsByDate(LocalDate targetDate) {
        List<Event> all = eventManager.getAllEventsExpanded();
        List<Event> res = new ArrayList<>();
        for (Event e : all) {
            if (e.getStartDateTimeAsLdt() == null) continue;
            if (e.getStartDateTimeAsLdt().toLocalDate().equals(targetDate)) res.add(e);
        }
        res.sort(Comparator.comparing(Event::getStartDateTimeAsLdt, Comparator.nullsLast(Comparator.naturalOrder())));
        return res;
    }

    public List<Event> searchEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Event> all = eventManager.getAllEventsExpanded();
        List<Event> res = new ArrayList<>();
        for (Event e : all) {
            if (e.getStartDateTimeAsLdt() == null) continue;
            LocalDate d = e.getStartDateTimeAsLdt().toLocalDate();
            if ((d.isAfter(startDate) || d.equals(startDate)) && (d.isBefore(endDate) || d.equals(endDate))) {
                res.add(e);
            }
        }
        res.sort(Comparator.comparing(Event::getStartDateTimeAsLdt, Comparator.nullsLast(Comparator.naturalOrder())));
        return res;
    }

    public List<Event> searchEventsByTitle(String keyword) {
        String k = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<Event> all = eventManager.getAllEventsExpanded();
        List<Event> res = new ArrayList<>();
        for (Event e : all) {
            String t = (e.getTitle() == null) ? "" : e.getTitle().toLowerCase();
            if (t.contains(k)) res.add(e);
        }
        return res;
    }

    public List<Event> filterEventsByCategory(String category) {
        String k = (category == null) ? "" : category.trim().toLowerCase();
        List<Event> all = eventManager.getAllEventsExpanded();
        List<Event> res = new ArrayList<>();
        for (Event e : all) {
            String c = (e.getCategory() == null) ? "" : e.getCategory().toLowerCase();
            if (c.equals(k)) res.add(e);
        }
        return res;
    }

    public List<Event> filterEventsByLocation(String location) {
        String k = (location == null) ? "" : location.trim().toLowerCase();
        List<Event> all = eventManager.getAllEventsExpanded();
        List<Event> res = new ArrayList<>();
        for (Event e : all) {
            String loc = (e.getLocation() == null) ? "" : e.getLocation().toLowerCase();
            if (loc.equals(k)) res.add(e);
        }
        return res;
    }
}