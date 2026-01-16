package calenderApplication.businessLogic;

import calenderApplication.dataLayer.EventIdGenerator;
import calenderApplication.dataLayer.FileIOManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private final FileIOManager ioManager;
    private ReminderManager reminderManager;
    
    // 内存缓存：Key 为 EventID
    private final Map<Integer, Event> eventCache = new HashMap<>();
    private final Map<Integer, RecurrentEvent> recurrentRulesByEventId = new HashMap<>();

    public EventManager(FileIOManager ioManager) {
        this.ioManager = ioManager;
        loadDataIntoMemory();
    }
    
    private void loadDataIntoMemory() {
        eventCache.clear();
        ioManager.readAllEventsFromCsv().forEach(e -> eventCache.put(e.getEventId(), e));

        recurrentRulesByEventId.clear();
        ioManager.readAllRecurrentEventsFromCsv().forEach(r -> {
            recurrentRulesByEventId.put(r.getEventId(), r);
        });
    }

public boolean createEvent(Event event, RecurrentEvent recurrentEvent) {
        if (!isEventValidForCreate(event)) return false;

        // 冲突检查（基于内存）
        if (!checkEventConflict(event).isEmpty()) return false;

        // 分配 ID 并写入文件
        int newId = EventIdGenerator.generateNextEventId();
        event.setEventId(newId);
        ioManager.writeEventToCsv(event);
        
        // 更新内存缓存
        eventCache.put(newId, event);

        if (recurrentEvent != null && recurrentEvent.isEnabled()) {
            recurrentEvent.setEventId(newId);
            ioManager.writeRecurrentEventToCsv(recurrentEvent);
            recurrentRulesByEventId.put(newId, recurrentEvent);
        }
        return true;
    }

public boolean updateEvent(Event event, RecurrentEvent recurrent) {
        if (!isEventValidForCreate(event)) return false;

        if (ioManager.updateEventInCsv(event)) {
            eventCache.put(event.getEventId(), event); // 刷新内存
            
            if (recurrent != null) {
                recurrent.setEventId(event.getEventId());
                ioManager.updateRecurrentEventInCsv(recurrent);
                recurrentRulesByEventId.put(event.getEventId(), recurrent);
            }
            return true;
        }
        return false;
    }

public boolean deleteEvent(int eventId) {
        boolean deleted = ioManager.deleteEventFromCsv(eventId);
        if (deleted) {
            // 同步清理内存
            eventCache.remove(eventId);
            recurrentRulesByEventId.remove(eventId);
            ioManager.deleteRecurrentEventFromCsv(eventId);
            
            if (this.reminderManager != null) {
                this.reminderManager.deleteReminder(eventId);
            }
            return true;
        }
        return false;
    }

    public List<Event> getEventsForDate(LocalDate date) {
        return getAllEventsExpanded().stream()
            .filter(e -> e.getStartDateTimeAsLdt() != null && 
                         e.getStartDateTimeAsLdt().toLocalDate().equals(date))
            .collect(Collectors.toList());
    }
    
    public List<Event> getAllEventsExpanded() {
        List<Event> expanded = new ArrayList<>(eventCache.values());
        for (Event base : eventCache.values()) {
            RecurrentEvent rule = recurrentRulesByEventId.get(base.getEventId());
            if (rule != null && rule.isEnabled()) {
                expanded.addAll(generateRecurrentEvents(base, rule));
            }
        }
        return expanded;
    }

    // --- recurrent generation (end <= endDate) ---
private List<Event> generateRecurrentEvents(Event base, RecurrentEvent rule) {
        List<Event> results = new ArrayList<>();
        int days = parseIntervalToDays(rule.getRecurrentInterval());
        if (days <= 0) return results;

        // 从 1 开始，因为 0 是基础事件本身
        for (int i = 1; i < rule.getRecurrentTimes(); i++) {
            results.add(cloneWithShift(base, i * days));
        }
        return results;
    }

public List<Event> checkEventConflict(Event newEvent) {
        LocalDateTime newStart = newEvent.getStartDateTimeAsLdt();
        LocalDateTime newEnd = newEvent.getEndDateTimeAsLdt();
        if (newStart == null || newEnd == null) return Collections.emptyList();

        return eventCache.values().stream()
            .filter(ex -> {
                // 排除正在编辑的事件本身
                if (ex.getEventId() == newEvent.getEventId()) return false;
                LocalDateTime exStart = ex.getStartDateTimeAsLdt();
                LocalDateTime exEnd = ex.getEndDateTimeAsLdt();
                return exStart != null && exEnd != null && 
                       newStart.isBefore(exEnd) && exStart.isBefore(newEnd);
            })
            .collect(Collectors.toList());
    }

    public Collection<Event> getAllBaseEvents() {
        return eventCache.values();
    }

    public RecurrentEvent getRecurrentRule(int eventId) {
        return recurrentRulesByEventId.get(eventId);
    }

    // --- 辅助私有方法 ---
    
    private int parseIntervalToDays(String interval) {
        if (interval == null) return 0;
        switch (interval.toLowerCase()) {
            case "1d": return 1;
            case "1w": return 7;
            case "2w": return 14;
            case "4w": return 28;
            default: return 0;
        }
    }
    
    private Event cloneWithShift(Event base, int shiftDays) {
        Event e = new Event();
        e.setEventId(base.getEventId());
        e.setTitle(base.getTitle() + " (R)"); // 标记为重复生成的
        e.setDescription(base.getDescription());
        e.setLocation(base.getLocation());
        e.setCategory(base.getCategory());
        e.setAttendees(new ArrayList<>(base.getAttendees()));

        if (base.getStartDateTimeAsLdt() != null) 
            e.setStartDateTime(base.getStartDateTimeAsLdt().plusDays(shiftDays));
        if (base.getEndDateTimeAsLdt() != null) 
            e.setEndDateTime(base.getEndDateTimeAsLdt().plusDays(shiftDays));
        return e;
    }

    private boolean isEventValidForCreate(Event e) {
        return e != null && e.getTitle() != null && !e.getTitle().trim().isEmpty()
               && e.getStartDateTimeAsLdt() != null && e.getEndDateTimeAsLdt() != null
               && e.getEndDateTimeAsLdt().isAfter(e.getStartDateTimeAsLdt());
    }

    public void setReminderManager(ReminderManager rm) {
        this.reminderManager = rm;
    }
    
}