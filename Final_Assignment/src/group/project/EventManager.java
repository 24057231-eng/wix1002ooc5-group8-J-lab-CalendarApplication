package group.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * EventManager (Business Logic Layer)
 *
 * Notes:
 * - Works with the provided Data Layer: File_IO + Event (Route B with attendees).
 * - Persists data in event.csv through File_IO.
 * - Uses String timestamps in Event, but parses them to LocalDateTime for validation and conflict checks.
 */
public class EventManager {

    // Keep this format consistent across your whole project (UI + BLL + DAL).
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final File_IO io;

    public EventManager(File_IO io) {
        this.io = io;
    }

    /**
     * Create a new event.
     * - Validates required fields
     * - Validates time format and time order
     * - Checks conflict with existing events
     * - Assigns a new ID if event_ID is 0
     * - Saves via File_IO.save_event(...)
     *
     * @param event the new event
     * @return result containing success flag + conflicts (if any) + error message (if failed)
     */
    public CreateResult createEvent(Event event) {
        String err = ValidationUtils.validateEventForCreateOrUpdate(event);
        if (err != null) return CreateResult.fail(err);

        List<Event> conflicts = checkEventConflict(event, /*ignoreEventId=*/-1);
        if (!conflicts.isEmpty()) {
            return CreateResult.conflict(conflicts);
        }

        // Assign ID if missing
        if (event.get_event_ID() == 0) {
            event.set_event_ID(io.get_ID());
        }

        io.save_event(event);
        return CreateResult.ok();
    }

    /**
     * Update an existing event by event_ID.
     * - Validates fields
     * - Checks conflict (ignoring itself)
     * - Loads all events and replaces the matched one, then rewrites event.csv
     */
    public UpdateResult updateEvent(Event updatedEvent) {
        String err = ValidationUtils.validateEventForCreateOrUpdate(updatedEvent);
        if (err != null) return UpdateResult.fail(err);

        if (updatedEvent.get_event_ID() <= 0) {
            return UpdateResult.fail("event_ID must be a positive integer for update.");
        }

        List<Event> all = io.read_event();
        int idx = indexOfEventById(all, updatedEvent.get_event_ID());
        if (idx < 0) {
            return UpdateResult.fail("Event not found: ID=" + updatedEvent.get_event_ID());
        }

        List<Event> conflicts = checkEventConflict(updatedEvent, /*ignoreEventId=*/updatedEvent.get_event_ID());
        if (!conflicts.isEmpty()) {
            return UpdateResult.conflict(conflicts);
        }

        all.set(idx, updatedEvent);
        io.rewrite_event(all);
        return UpdateResult.ok();
    }

    /**
     * Delete an event by ID (simple delete, no series handling).
     */
    public DeleteResult deleteEvent(int eventId) {
        if (eventId <= 0) return DeleteResult.fail("eventId must be positive.");

        List<Event> all = io.read_event();
        int before = all.size();

        all.removeIf(e -> e.get_event_ID() == eventId);

        if (all.size() == before) {
            return DeleteResult.fail("Event not found: ID=" + eventId);
        }

        io.rewrite_event(new ArrayList<>(all));
        return DeleteResult.ok();
    }

    public ArrayList<Event> getAllEvents() {
        return io.read_event();
    }

    /**
     * Conflict rule:
     * Two events conflict if their time ranges overlap:
     * (startA < endB) && (startB < endA)
     */
    public List<Event> checkEventConflict(Event newEvent, int ignoreEventId) {
        LocalDateTime ns = parseTime(newEvent.get_start_time());
        LocalDateTime ne = parseTime(newEvent.get_end_time());
        ArrayList<Event> all = io.read_event();

        ArrayList<Event> conflicts = new ArrayList<>();
        for (Event e : all) {
            if (ignoreEventId > 0 && e.get_event_ID() == ignoreEventId) continue;

            LocalDateTime es = parseTimeSafe(e.get_start_time());
            LocalDateTime ee = parseTimeSafe(e.get_end_time());
            if (es == null || ee == null) continue; // skip broken records

            boolean overlap = ns.isBefore(ee) && es.isBefore(ne);
            if (overlap) conflicts.add(e);
        }
        return conflicts;
    }

    public static LocalDateTime parseTime(String s) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(s.trim(), TIME_FMT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid time format. Expected: yyyy-MM-dd HH:mm");
        }
    }

    public static LocalDateTime parseTimeSafe(String s) {
        try { return parseTime(s); } catch (Exception e) { return null; }
    }

    public static LocalDate toDate(LocalDateTime dt) {
        return dt.toLocalDate();
    }

    private static int indexOfEventById(List<Event> list, int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get_event_ID() == id) return i;
        }
        return -1;
    }

    // -------- Result Types --------

    public static class CreateResult {
        public final boolean success;
        public final boolean hasConflict;
        public final List<Event> conflicts;
        public final String errorMessage;

        private CreateResult(boolean success, boolean hasConflict, List<Event> conflicts, String errorMessage) {
            this.success = success;
            this.hasConflict = hasConflict;
            this.conflicts = conflicts;
            this.errorMessage = errorMessage;
        }

        public static CreateResult ok() { return new CreateResult(true, false, new ArrayList<>(), null); }
        public static CreateResult conflict(List<Event> conflicts) { return new CreateResult(false, true, conflicts, null); }
        public static CreateResult fail(String msg) { return new CreateResult(false, false, new ArrayList<>(), msg); }
    }

    public static class UpdateResult {
        public final boolean success;
        public final boolean hasConflict;
        public final List<Event> conflicts;
        public final String errorMessage;

        private UpdateResult(boolean success, boolean hasConflict, List<Event> conflicts, String errorMessage) {
            this.success = success;
            this.hasConflict = hasConflict;
            this.conflicts = conflicts;
            this.errorMessage = errorMessage;
        }

        public static UpdateResult ok() { return new UpdateResult(true, false, new ArrayList<>(), null); }
        public static UpdateResult conflict(List<Event> conflicts) { return new UpdateResult(false, true, conflicts, null); }
        public static UpdateResult fail(String msg) { return new UpdateResult(false, false, new ArrayList<>(), msg); }
    }

    public static class DeleteResult {
        public final boolean success;
        public final String errorMessage;

        private DeleteResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static DeleteResult ok() { return new DeleteResult(true, null); }
        public static DeleteResult fail(String msg) { return new DeleteResult(false, msg); }
    }
}
