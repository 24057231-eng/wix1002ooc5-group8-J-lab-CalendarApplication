package group.project;

/**
 * ReminderConfig (Model)
 *
 * In your current Data Layer, reminders are stored via:
 *   File_IO.save_reminder(Reminder r)  -> reminder.csv: ID|time|message
 *
 * This class provides a more "config-like" structure to align with the assignment design,
 * while still being easy to map into Reminder for persistence.
 */
public class ReminderConfig {

    private int eventId;
    private int minutesBeforeStart; // e.g. 10 means remind 10 minutes before event start
    private boolean enable;

    public ReminderConfig() {}

    public ReminderConfig(int eventId, int minutesBeforeStart, boolean enable) {
        this.eventId = eventId;
        this.minutesBeforeStart = minutesBeforeStart;
        this.enable = enable;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getMinutesBeforeStart() { return minutesBeforeStart; }
    public void setMinutesBeforeStart(int minutesBeforeStart) { this.minutesBeforeStart = minutesBeforeStart; }

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }
}
