package group.project;

import java.time.Duration;

public class ReminderConfig {
    private int eventId;
    private Duration remindDuration; // PT30M, P1D etc.
    private boolean enable;

    public ReminderConfig() {
        this.remindDuration = Duration.ofMinutes(30);
        this.enable = false;
    }

    public ReminderConfig(int eventId, Duration remindDuration, boolean isEnable) {
        this.eventId = eventId;
        this.remindDuration = remindDuration;
        this.enable = isEnable;
    }

    public int getEventId() { return eventId; }
    public Duration getRemindDuration() { return remindDuration; }
    public boolean isEnable() { return enable; }

    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setRemindDuration(Duration remindDuration) { this.remindDuration = remindDuration; }
    public void setEnable(boolean enable) { this.enable = enable; }
}