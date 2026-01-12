package group.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ReminderManager (Business Logic Layer)
 *
 * Works with:
 * - EventManager + File_IO (events)
 * - Reminder + File_IO (reminders)
 *
 * Current Data Layer reminder storage:
 *   reminder.csv line: eventId|time|message
 * where time is a string in format yyyy-MM-dd HH:mm.
 */
public class ReminderManager {

    private final File_IO io;

    public ReminderManager(File_IO io) {
        this.io = io;
    }

    /**
     * Create/overwrite a reminder for a given event using ReminderConfig.
     *
     * Rule:
     * - If config.enable == false -> do nothing (or you can decide to remove reminder in future).
     * - Reminder time = event.start_time - minutesBeforeStart
     */
    public String setReminder(ReminderConfig config) {
        if (config == null) return "ReminderConfig is null.";
        if (!config.isEnable()) return null; // disabled => no action

        ArrayList<Event> all = io.read_event();
        Event target = null;
        for (Event e : all) {
            if (e.get_event_ID() == config.getEventId()) {
                target = e;
                break;
            }
        }
        if (target == null) return "Event not found: ID=" + config.getEventId();

        LocalDateTime start = EventManager.parseTimeSafe(target.get_start_time());
        if (start == null) return "Event start_time format invalid for ID=" + config.getEventId();

        int mins = Math.max(0, config.getMinutesBeforeStart());
        LocalDateTime remindAt = start.minusMinutes(mins);

        Reminder r = new Reminder();
        r.set_event_ID(config.getEventId());
        r.set_time(remindAt.format(EventManager.TIME_FMT));
        r.set_message("Reminder: " + target.get_title() + " starts at " + target.get_start_time());

        io.save_reminder(r);
        return null;
    }

    /**
     * Return reminders whose reminder time is within the next N minutes (inclusive).
     *
     * @param now current time
     * @param withinMinutes non-negative window
     */
    public List<Reminder> getUpcomingReminders(LocalDateTime now, int withinMinutes) {
        if (now == null) now = LocalDateTime.now();
        int w = Math.max(0, withinMinutes);

        ArrayList<Reminder> all = io.read_reminder();
        ArrayList<Reminder> res = new ArrayList<>();

        for (Reminder r : all) {
            LocalDateTime rt = EventManager.parseTimeSafe(r.get_time());
            if (rt == null) continue;
            boolean inWindow = (!rt.isBefore(now)) && (!rt.isAfter(now.plusMinutes(w)));
            if (inWindow) res.add(r);
        }
        return res;
    }
}
