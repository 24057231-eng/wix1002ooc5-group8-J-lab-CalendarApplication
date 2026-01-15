package group.project;

import java.time.LocalDate;

public class RecurrentEvent {
    private int eventId;
    private String recurrentInterval; // e.g. 1d, 1w, 2w
    private int recurrentTimes;       // 0 means ignore times, use endDate
    private String recurrentEndDate;  // stored as String to match FileIOManager, e.g. 2026-01-31 or "null" / ""

    public RecurrentEvent() {}

    public RecurrentEvent(int eventId, String recurrentInterval, int recurrentTimes, String recurrentEndDate) {
        this.eventId = eventId;
        this.recurrentInterval = recurrentInterval;
        this.recurrentTimes = recurrentTimes;
        this.recurrentEndDate = recurrentEndDate;
    }

    public int getEventId() { return eventId; }
    public String getRecurrentInterval() { return recurrentInterval; }
    public int getRecurrentTimes() { return recurrentTimes; }
    public String getRecurrentEndDate() { return recurrentEndDate; }

    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setRecurrentInterval(String recurrentInterval) { this.recurrentInterval = recurrentInterval; }
    public void setRecurrentTimes(int recurrentTimes) { this.recurrentTimes = recurrentTimes; }
    public void setRecurrentEndDate(String recurrentEndDate) { this.recurrentEndDate = recurrentEndDate; }

    public boolean isEnabled() {
        return recurrentInterval != null && !recurrentInterval.trim().isEmpty();
    }

    /** Business helper: parse endDate string to LocalDate, return null if not usable */
    public LocalDate getRecurrentEndDateAsLocalDate() {
        if (recurrentEndDate == null) return null;
        String s = recurrentEndDate.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}