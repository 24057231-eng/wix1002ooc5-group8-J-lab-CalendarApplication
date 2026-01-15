package group.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class EventManager {
    private final FileIOManager ioManager;

    // In-memory stores for recurrent/reminder until FileIOManager supports them
    private final Map<Integer, RecurrentEvent> recurrentRulesByEventId = new HashMap<>();

    public EventManager(FileIOManager ioManager) {
        this.ioManager = ioManager;
    }

    // --- Spec: createEvent ---
    public boolean createEvent(Event event, RecurrentEvent recurrentEvent) {
        if (!isEventValidForCreate(event)) return false;

        // conflict check (optional but recommended)
        if (!checkEventConflict(event).isEmpty()) return false;

        int newId = EventIdGenerator.generateNextEventId(ioManager);
        event.setEventId(newId);

        ioManager.writeEventToCsv(event);

        if (recurrentEvent != null && recurrentEvent.isEnabled()) {
            recurrentEvent.setEventId(newId);
            recurrentRulesByEventId.put(newId, recurrentEvent);
        }
        return true;
    }

    // --- Spec: updateEvent ---
    public boolean updateEvent(Event updatedEvent, RecurrentEvent newRecurrentRule) {
        if (updatedEvent == null || updatedEvent.getEventId() <= 0) return false;
        if (!updatedEvent.isTimeValid()) return false;
        if (updatedEvent.getTitle() == null || updatedEvent.getTitle().trim().isEmpty()) return false;

        // conflict check: ignore itself
        List<Event> conflicts = checkEventConflict(updatedEvent);
        conflicts.removeIf(e -> e.getEventId() == updatedEvent.getEventId());
        if (!conflicts.isEmpty()) return false;

        boolean ok = ioManager.updateEventInCsv(updatedEvent);

        if (ok) {
            if (newRecurrentRule != null && newRecurrentRule.isEnabled()) {
                newRecurrentRule.setEventId(updatedEvent.getEventId());
                recurrentRulesByEventId.put(updatedEvent.getEventId(), newRecurrentRule);
            } else {
                recurrentRulesByEventId.remove(updatedEvent.getEventId());
            }
        }
        return ok;
    }

    // --- Spec: deleteEvent ---
    public boolean deleteEvent(int eventId, boolean isDeleteEntireSeries) {
        if (eventId <= 0) return false;

        // With current persistence, deleting entire series == deleting base + removing rule
        boolean ok = ioManager.deleteEventFromCsv(eventId);
        if (ok) {
            recurrentRulesByEventId.remove(eventId);
        }
        return ok;
    }

    // --- Spec: generateRecurrentEvents ---
    public List<Event> generateRecurrentEvents(Event baseEvent, RecurrentEvent recurrentRule) {
        if (baseEvent == null || recurrentRule == null || !recurrentRule.isEnabled()) return Collections.emptyList();
        if (baseEvent.getStartDateTimeAsLdt() == null || baseEvent.getEndDateTimeAsLdt() == null) return Collections.emptyList();

        int stepDays = parseIntervalToDays(recurrentRule.getRecurrentInterval());
        if (stepDays <= 0) return Collections.emptyList();

        List<Event> result = new ArrayList<>();

        LocalDateTime start = baseEvent.getStartDateTimeAsLdt();
        LocalDateTime end = baseEvent.getEndDateTimeAsLdt();

        int times = recurrentRule.getRecurrentTimes();
        LocalDate endDate = recurrentRule.getRecurrentEndDate();

        // Always include the base event itself as the first occurrence
        result.add(cloneWithShift(baseEvent, 0));

        if (times > 0) {
            for (int i = 1; i < times; i++) {
                result.add(cloneWithShift(baseEvent, stepDays * i));
            }
        } else if (endDate != null) {
            int i = 1;
            while (start.plusDays((long) stepDays * i).toLocalDate().isAfter(endDate) == false) {
                result.add(cloneWithShift(baseEvent, stepDays * i));
                i++;
            }
        }

        return result;
    }

    // --- Spec: checkEventConflict ---
    public List<Event> checkEventConflict(Event newEvent) {
        if (newEvent == null || newEvent.getStartDateTimeAsLdt() == null || newEvent.getEndDateTimeAsLdt() == null) {
            return Collections.emptyList();
        }

        List<Event> all = getAllEventsExpanded();
        List<Event> conflicts = new ArrayList<>();

        for (Event e : all) {
            // Overlap rule: (A.start < B.end) && (B.start < A.end)
            if (e.getStartDateTimeAsLdt() == null || e.getEndDateTimeAsLdt() == null) continue;
            boolean overlap = newEvent.getStartDateTimeAsLdt().isBefore(e.getEndDateTimeAsLdt())
                    && e.getStartDateTimeAsLdt().isBefore(newEvent.getEndDateTimeAsLdt());
            if (overlap) conflicts.add(e);
        }
        return conflicts;
    }

    // --- Utilities used by other managers ---
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
        if (!event.isTimeValid()) return false;
        return true;
    }

    private int parseIntervalToDays(String interval) {
        if (interval == null) return -1;
        String s = interval.trim().toLowerCase();

        // Minimal supported: Nd / Nw
        try {
            if (s.endsWith("d")) {
                int n = Integer.parseInt(s.substring(0, s.length() - 1));
                return n;
            }
            if (s.endsWith("w")) {
                int n = Integer.parseInt(s.substring(0, s.length() - 1));
                return 7 * n;
            }
        } catch (NumberFormatException ignored) {}
        return -1;
    }

    private Event cloneWithShift(Event base, int shiftDays) {
        Event e = new Event();
        e.setEventId(base.getEventId()); // keep series id for simplicity
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