/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;

import java.time.LocalDate;
import java.util.*;

public class EventManager {
    // Map to store events: Date -> List of Strings
    private Map<LocalDate, List<String>> eventData = new HashMap<>();

    public void addEvent(LocalDate date, String event) {
        eventData.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
    }

    public void deleteEvent(LocalDate date, int index) {
        List<String> events = eventData.get(date);
        if (events != null && index >= 0 && index < events.size()) {
            events.remove(index);
        }
    }

    public List<String> getEvents(LocalDate date) {
        return eventData.getOrDefault(date, new ArrayList<>());
    }
}