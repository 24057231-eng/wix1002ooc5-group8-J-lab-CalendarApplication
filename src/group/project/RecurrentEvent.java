package group.project;

/**
 * RecurrentEvent (Model)
 *
 * This model matches the current Data Layer method:
 *   File_IO.save_recurrent(int ID, String interval, int times, String end_time)
 *
 * Storage idea (recurrent.csv):
 *   ID|interval|times|end_time
 *
 * interval example: "1d" (daily), "1w" (weekly), "1m" (monthly)
 * end_time format: yyyy-MM-dd HH:mm (same as EventManager.TIME_FMT)
 */
public class RecurrentEvent {

    private int eventId;
    private String recurrentInterval; // e.g. 1d, 1w
    private int recurrentTimes;       // repeat count (0 means unknown/unused)
    private String recurrentEndTime;  // end timestamp string

    public RecurrentEvent() {}

    public RecurrentEvent(int eventId, String interval, int times, String endTime) {
        this.eventId = eventId;
        this.recurrentInterval = interval;
        this.recurrentTimes = times;
        this.recurrentEndTime = endTime;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getRecurrentInterval() { return recurrentInterval; }
    public void setRecurrentInterval(String interval) { this.recurrentInterval = interval; }

    public int getRecurrentTimes() { return recurrentTimes; }
    public void setRecurrentTimes(int times) { this.recurrentTimes = times; }

    public String getRecurrentEndTime() { return recurrentEndTime; }
    public void setRecurrentEndTime(String endTime) { this.recurrentEndTime = endTime; }
}
