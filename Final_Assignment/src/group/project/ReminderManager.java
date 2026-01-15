package group.project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ReminderManager {
    private final EventManager eventManager;

    // In-memory reminder configs (until FileIOManager supports reminder.csv)
    private final Map<Integer, ReminderConfig> reminderByEventId = new HashMap<>();

    public ReminderManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setReminder(ReminderConfig config) {
        if (config == null || config.getEventId() <= 0) return;
        if (config.getRemindDuration() == null) config.setRemindDuration(Duration.ofMinutes(30));
        reminderByEventId.put(config.getEventId(), config);
    }

    public List<String> getUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<String> res = new ArrayList<>();

        // We check base events only (series id matches); you can also expand if needed
        for (Event e : eventManager.getAllBaseEvents()) {
            ReminderConfig cfg = reminderByEventId.get(e.getEventId());
            if (cfg == null || !cfg.isEnable()) continue;

            if (e.getStartDateTimeAsLdt() == null) continue;
            LocalDateTime remindAt = e.getStartDateTimeAsLdt().minus(cfg.getRemindDuration());

            // If now is after remindAt and before event start -> should remind
            if ((now.isAfter(remindAt) || now.equals(remindAt)) && now.isBefore(e.getStartDateTimeAsLdt())) {
                long mins = Math.max(0, Duration.between(now, e.getStartDateTimeAsLdt()).toMinutes());
                res.add("Your next event is coming soon in " + mins + " minutes: " + e.getTitle());
            }
        }

        return res;
    }

    public ReminderConfig getReminderConfig(int eventId) {
        return reminderByEventId.get(eventId);
    }
}