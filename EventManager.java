/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;


import java.time.LocalDate;
import java.util.*;

public class EventManager {
    // Map to store date and its corresponding list of events
    private Map<LocalDate, List<String>> database = new HashMap<>();

    public void addEvent(LocalDate date, String task) {
        database.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
    }

    public List<String> getEvents(LocalDate date) {
        return database.getOrDefault(date, new ArrayList<>());
    }
}