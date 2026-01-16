package calenderApplication.businessLogic;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    // Additional Event Fields
    private String location;
    private String category;
    private List<String> attendees = new ArrayList<>();

    // --- Constructors (spec) ---
    public Event() {}

    public Event(String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // --- Getters (spec) ---
    public int getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartDateTimeAsLdt() { return startDateTime; }
    public LocalDateTime getEndDateTimeAsLdt() { return endDateTime; }

    // Compatibility getter for existing FileIOManager which concatenates strings
    public String getStartDateTime() { return startDateTime == null ? "" : startDateTime.toString(); }
    public String getEndDateTime() { return endDateTime == null ? "" : endDateTime.toString(); }

    public String getLocation() { return location; }
    public List<String> getAttendees() { return Collections.unmodifiableList(attendees); }
    public String getCategory() { return category; }

    // --- Setters (spec) ---
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    // Compatibility setters used by FileIOManager (String -> LocalDateTime)
    public void setStartDateTime(String iso) { this.startDateTime = parseIso(iso); }
    public void setEndDateTime(String iso) { this.endDateTime = parseIso(iso); }

    public void setLocation(String location) { this.location = location; }
    public void setAttendees(List<String> attendees) {
        this.attendees = (attendees == null) ? new ArrayList<>() : new ArrayList<>(attendees);
    }
    public void setCategory(String category) { this.category = category; }

    // --- Helpers ---
    private LocalDateTime parseIso(String iso) {
        if (iso == null || iso.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(iso.trim());
        } catch (DateTimeParseException ex) {
            // Fallback: treat as null to avoid crashing IO parse
            return null;
        }
    }

    public boolean isTimeValid() {
        return startDateTime != null && endDateTime != null && startDateTime.isBefore(endDateTime);
    }
}