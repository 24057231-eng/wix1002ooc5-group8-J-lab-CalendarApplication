package calenderApplication.businessLogic;

import calenderApplication.dataLayer.FileIOManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ReminderManager {
    private final EventManager eventManager;
    private final FileIOManager ioManager;
    private final Map<Integer, ReminderConfig> reminderByEventId = new HashMap<>();

    public ReminderManager(EventManager eventManager, FileIOManager ioManager) {
        this.eventManager = eventManager;
        this.ioManager = ioManager;

        // load from reminder.csv; if duplicates exist, last one wins
        for (ReminderConfig c : ioManager.readAllReminderConfigs()) {
            reminderByEventId.put(c.getEventId(), c);
        }
    }

    public void setReminder(ReminderConfig config) {
        if (config == null || config.getEventId() <= 0) return;
        if (config.getRemindDuration() == null) {
            config.setRemindDuration(Duration.ofMinutes(30));
        }

        reminderByEventId.put(config.getEventId(), config);

        // persistent append (your FileIOManager writes append)
        ioManager.writeReminderConfigToFile(config);
    }

    public void disableReminder(int eventId) {
        if (eventId <= 0) return;
        ReminderConfig c = reminderByEventId.get(eventId);
        if (c != null) {
            c.setEnable(false);
            setReminder(c);
        }
    }

    public List<String> getUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<String> res = new ArrayList<>();

        for (Event e : eventManager.getAllBaseEvents()) {
            ReminderConfig cfg = reminderByEventId.get(e.getEventId());
            if (cfg == null || !cfg.isEnable()) continue;
            if (e.getStartDateTimeAsLdt() == null) continue;

            Duration d = cfg.getRemindDurationAsDuration();
            LocalDateTime remindAt = e.getStartDateTimeAsLdt().minus(d);

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
    
public void deleteReminder(int eventId) {
    if (eventId <= 0) return;

    reminderByEventId.remove(eventId);

    ioManager.deleteReminderConfigFromCsv(eventId);
}
}