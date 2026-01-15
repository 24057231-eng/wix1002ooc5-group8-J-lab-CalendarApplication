package group.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class EventManager {
    private final FileIOManager ioManager;
    private final Map<Integer, RecurrentEvent> recurrentRulesByEventId = new HashMap<>();

    public EventManager(FileIOManager ioManager) {
        this.ioManager = ioManager;

        // load from recurrent.csv (no more memory compensation)
        for (RecurrentEvent r : ioManager.readAllRecurrentEventsFromCsv()) {
            recurrentRulesByEventId.put(r.getEventId(), r);
        }
    }

    public boolean createEvent(Event event, RecurrentEvent recurrentEvent) {
        if (!isEventValidForCreate(event)) return false;

        // Optional: conflict check
        if (!checkEventConflict(event).isEmpty()) return false;

        int newId = EventIdGenerator.generateNextEventId();
        event.setEventId(newId);
        ioManager.writeEventToCsv(event);

        if (recurrentEvent != null && recurrentEvent.isEnabled()) {
            recurrentEvent.setEventId(newId);
            recurrentRulesByEventId.put(newId, recurrentEvent);
            ioManager.writeRecurrentEventToCsv(recurrentEvent);
        }
        return true;
    }

    public boolean updateEvent(Event updatedEvent, RecurrentEvent newRecurrentRule) {
        if (updatedEvent == null || updatedEvent.getEventId() <= 0) return false;
        if (!updatedEvent.isTimeValid()) return false;
        if (updatedEvent.getTitle() == null || updatedEvent.getTitle().trim().isEmpty()) return false;

        // conflict check ignore itself
        List<Event> conflicts = checkEventConflict(updatedEvent);
        conflicts.removeIf(e -> e.getEventId() == updatedEvent.getEventId());
        if (!conflicts.isEmpty()) return false;

        boolean ok = ioManager.updateEventInCsv(updatedEvent);
        if (!ok) return false;

        int id = updatedEvent.getEventId();

        if (newRecurrentRule != null && newRecurrentRule.isEnabled()) {
            newRecurrentRule.setEventId(id);
            recurrentRulesByEventId.put(id, newRecurrentRule);

            // persistent update: try update first, if not found then append
            boolean updated = ioManager.updateRecurrentEventInCsv(newRecurrentRule);
            if (!updated) ioManager.writeRecurrentEventToCsv(newRecurrentRule);

        } else {
            recurrentRulesByEventId.remove(id);
            ioManager.deleteRecurrentEventFromCsv(id);
        }

        return true;
    }

    public boolean deleteEvent(int eventId, boolean isDeleteEntireSeries) {
        if (eventId <= 0) return false;

        boolean ok = ioManager.deleteEventFromCsv(eventId);
        if (!ok) return false;

        // current storage is series-based: delete rule as well
        recurrentRulesByEventId.remove(eventId);
        ioManager.deleteRecurrentEventFromCsv(eventId);
        return true;
    }

    // --- recurrent generation (end <= endDate) ---
    public List<Event> generateRecurrentEvents(Event baseEvent, RecurrentEvent recurrentRule) {
        if (baseEvent == null || recurrentRule == null || !recurrentRule.isEnabled()) return Collections.emptyList();
        if (baseEvent.getStartDateTimeAsLdt() == null || baseEvent.getEndDateTimeAsLdt() == null) return Collections.emptyList();

        int stepDays = parseIntervalToDays(recurrentRule.getRecurrentInterval());
        if (stepDays <= 0) return Collections.emptyList();

        List<Event> result = new ArrayList<>();
        result.add(cloneWithShift(baseEvent, 0)); // base occurrence

        int times = recurrentRule.getRecurrentTimes();
        LocalDate endDate = recurrentRule.getRecurrentEndDateAsLocalDate();

        // by times (if >0)
        if (times > 0) {
            for (int i = 1; i < times; i++) {
                result.add(cloneWithShift(baseEvent, stepDays * i));
            }
            return result;
        }

        // by end date (end <= endDate)
        if (endDate != null) {
            LocalDateTime baseEnd = baseEvent.getEndDateTimeAsLdt();
            int i = 1;
            while (true) {
                LocalDateTime nextEnd = baseEnd.plusDays((long) stepDays * i);
                if (nextEnd.toLocalDate().isAfter(endDate)) break;
                result.add(cloneWithShift(baseEvent, stepDays * i));
                i++;
            }
        }

        return result;
    }

    public List<Event> checkEventConflict(Event newEvent) {
        if (newEvent == null || newEvent.getStartDateTimeAsLdt() == null || newEvent.getEndDateTimeAsLdt() == null) {
            return Collections.emptyList();
        }

        List<Event> all = getAllEventsExpanded();
        List<Event> conflicts = new ArrayList<>();

        for (Event e : all) {
            if (e.getStartDateTimeAsLdt() == null || e.getEndDateTimeAsLdt() == null) continue;

            boolean overlap = newEvent.getStartDateTimeAsLdt().isBefore(e.getEndDateTimeAsLdt())
                    && e.getStartDateTimeAsLdt().isBefore(newEvent.getEndDateTimeAsLdt());
            if (overlap) conflicts.add(e);
        }
        return conflicts;
    }

    public List<Event> getAllBaseEvents() {
        return ioManager.readAllEventsFromCsv();
    }

    public RecurrentEvent getRecurrentRule(int eventId) {
        return recurrentRulesByEventId.get(eventId);
    }

    public List<Event> getAllEventsExpanded() {
        List<Event> base = getAllBaseEvents();
        List<Event> expanded = new ArrayList<>();
        for (Event e : base) {
            RecurrentEvent rule = recurrentRulesByEventId.get(e.getEventId());
            if (rule != null && rule.isEnabled()) expanded.addAll(generateRecurrentEvents(e, rule));
            else expanded.add(e);
        }
        return expanded;
    }

    private boolean isEventValidForCreate(Event event) {
        if (event == null) return false;
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) return false;
        return event.isTimeValid();
    }

    private int parseIntervalToDays(String interval) {
        if (interval == null) return -1;
        String s = interval.trim().toLowerCase();
        try {
            if (s.endsWith("d")) return Integer.parseInt(s.substring(0, s.length() - 1));
            if (s.endsWith("w")) return 7 * Integer.parseInt(s.substring(0, s.length() - 1));
        } catch (Exception ignored) {}
        return -1;
    }

    private Event cloneWithShift(Event base, int shiftDays) {
        Event e = new Event();
        e.setEventId(base.getEventId()); // series id
        e.setTitle(base.getTitle());
        e.setDescription(base.getDescription());
        e.setLocation(base.getLocation());
        e.setCategory(base.getCategory());
        e.setAttendees(new ArrayList<>(base.getAttendees()));

        if (base.getStartDateTimeAsLdt() != null) e.setStartDateTime(base.getStartDateTimeAsLdt().plusDays(shiftDays));
        if (base.getEndDateTimeAsLdt() != null) e.setEndDateTime(base.getEndDateTimeAsLdt().plusDays(shiftDays));
        return e;
    }
}