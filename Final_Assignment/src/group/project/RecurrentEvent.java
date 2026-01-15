package group.project;

import java.time.LocalDate;

public class RecurrentEvent {
    private int eventId;
    private String recurrentInterval;  // e.g. 1d, 1w, 2w
    private int recurrentTimes;        // 0 means use endDate
    private LocalDate recurrentEndDate;// null means use times

    public RecurrentEvent() {}

    public RecurrentEvent(int eventId, String recurrentInterval, int recurrentTimes, LocalDate recurrentEndDate) {
        this.eventId = eventId;
        this.recurrentInterval = recurrentInterval;
        this.recurrentTimes = recurrentTimes;
        this.recurrentEndDate = recurrentEndDate;
    }

    public int getEventId() { return eventId; }
    public String getRecurrentInterval() { return recurrentInterval; }
    public int getRecurrentTimes() { return recurrentTimes; }
    public LocalDate getRecurrentEndDate() { return recurrentEndDate; }

    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setRecurrentInterval(String recurrentInterval) { this.recurrentInterval = recurrentInterval; }
    public void setRecurrentTimes(int recurrentTimes) { this.recurrentTimes = recurrentTimes; }
    public void setRecurrentEndDate(LocalDate recurrentEndDate) { this.recurrentEndDate = recurrentEndDate; }

    public boolean isEnabled() {
        return recurrentInterval != null && !recurrentInterval.trim().isEmpty();
    }
}