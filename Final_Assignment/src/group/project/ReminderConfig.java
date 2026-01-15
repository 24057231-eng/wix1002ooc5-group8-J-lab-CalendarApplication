package group.project;

import java.time.Duration;

public class ReminderConfig {
    private int eventId;
    private String remindDuration; // stored as String (e.g. PT30M or "30") to match FileIOManager
    private boolean enable;

    public ReminderConfig() {
        this.remindDuration = "PT30M";
        this.enable = false;
    }

    public ReminderConfig(int eventId, String remindDuration, boolean enable) {
        this.eventId = eventId;
        this.remindDuration = remindDuration;
        this.enable = enable;
    }

    public int getEventId() { return eventId; }
    public String getRemindDuration() { return remindDuration; }
    public boolean isEnable() { return enable; }

    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setRemindDuration(String remindDuration) { this.remindDuration = remindDuration; }
    public void setEnable(boolean enable) { this.enable = enable; }

    /** Business helper: parse to Duration. Accept ISO-8601 Duration (PT30M) or plain minutes ("30"). */
    public Duration getRemindDurationAsDuration() {
        if (remindDuration == null) return Duration.ofMinutes(30);
        String s = remindDuration.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return Duration.ofMinutes(30);

        // Prefer ISO-8601 duration
        try {
            return Duration.parse(s);
        } catch (Exception ignored) {}

        // Fallback: minutes number
        try {
            long mins = Long.parseLong(s);
            return Duration.ofMinutes(Math.max(0, mins));
        } catch (Exception ignored) {}

        return Duration.ofMinutes(30);
    }
}
