/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.dataLayer;

import calenderApplication.businessLogic.Event;
import calenderApplication.businessLogic.RecurrentEvent;
import calenderApplication.businessLogic.ReminderConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author 星飞
 */
public class FileIOManager {
    private final String eventPath = "event.csv";
    private final String recurrentPath = "recurrent.csv";
    private final String reminderPath = "reminder.csv";

    //Event
    public synchronized void writeEventToCsv(Event event) {
        //  ID|Title|Description|StartTime|EndTime|Location|Category
        String line = event.getEventId() + "|" + 
                      event.getTitle() + "|" + 
                      event.getDescription() + "|" + 
                      event.getStartDateTime() + "|" + 
                      event.getEndDateTime() + "|" + 
                      event.getLocation() + "|" + 
                      event.getCategory();
        writeLineToFile(eventPath, line);
    }

    public List<Event> readAllEventsFromCsv() {
        List<Event> list = new ArrayList<>();
        File f = new File(eventPath);
        if (!f.exists()) return list;

        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 7) {
                    Event ev = new Event();
                    ev.setEventId(Integer.parseInt(p[0]));
                    ev.setTitle(p[1]);
                    ev.setDescription(p[2]);
                    ev.setStartDateTime(LocalDateTime.parse(p[3]));
                    ev.setEndDateTime(LocalDateTime.parse(p[4]));
                    ev.setLocation(p[5]);
                    ev.setCategory(p[6]);
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading events: " + e.getMessage());
        }
        return list;
    }

    //      RecurrentEvent
    public synchronized void writeRecurrentEventToCsv(RecurrentEvent rc) {
        String line = rc.getEventId() + "|" + 
                      rc.getRecurrentInterval() + "|" + 
                      rc.getRecurrentTimes() + "|" + 
                      rc.getRecurrentEndDate();
        writeLineToFile(recurrentPath, line);
    }

    public List<RecurrentEvent> readAllRecurrentEventsFromCsv() {
        List<RecurrentEvent> list = new ArrayList<>();
        File f = new File(recurrentPath);
        if (!f.exists()) return list;

        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 4) {
                    list.add(new RecurrentEvent(
                        Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3]
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading recurrences: " + e.getMessage());
        }
        return list;
    }

    //      ReminderConfig
    public synchronized void writeReminderConfigToFile(ReminderConfig rm) {
        String line = rm.getEventId() + "|" + 
                      rm.getRemindDuration() + "|" + 
                      rm.isEnable();
        writeLineToFile(reminderPath, line);
    }

public List<ReminderConfig> readAllReminderConfigs() {
        List<ReminderConfig> list = new ArrayList<>();
        File f = new File(reminderPath);
        if (!f.exists()) return list;

        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 3) {
                    list.add(new ReminderConfig(
                        Integer.parseInt(p[0]), 
                        Duration.parse(p[1]), 
                        Boolean.parseBoolean(p[2])
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading reminders: " + e.getMessage());
        }
        return list;
    }

    private void writeLineToFile(String filePath, String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("IO Error on " + filePath + ": " + e.getMessage());
        }
    }

    public synchronized boolean updateEventInCsv(Event updatedEvent) {
        List<Event> allEvents = readAllEventsFromCsv();
        boolean found = false;
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(eventPath, false))) { 
            for (Event e : allEvents) {
                if (e.getEventId() == updatedEvent.getEventId()) {
                    pw.println(eventToCsvLine(updatedEvent));
                    found = true;
                } else {
                    pw.println(eventToCsvLine(e));
                }
            }
        } catch (IOException e) {
            System.err.println("Update Event Error: " + e.getMessage());
            return false;
        }
        return found;
    }

    public synchronized boolean deleteEventFromCsv(int eventId) {
        List<Event> allEvents = readAllEventsFromCsv();
        try (PrintWriter pw = new PrintWriter(new FileWriter(eventPath, false))) {
            for (Event e : allEvents) {
                if (e.getEventId() != eventId) {
                    pw.println(eventToCsvLine(e));
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Delete Event Error: " + e.getMessage());
            return false;
        }
    }


    public synchronized boolean updateRecurrentEventInCsv(RecurrentEvent updatedRc) {
        List<RecurrentEvent> allRules = readAllRecurrentEventsFromCsv(); 
        boolean found = false;
        try (PrintWriter pw = new PrintWriter(new FileWriter(recurrentPath, false))) {
            for (RecurrentEvent rc : allRules) {
                if (rc.getEventId() == updatedRc.getEventId()) {
                    pw.println(recurrentToCsvLine(updatedRc));
                    found = true;
                } else {
                    pw.println(recurrentToCsvLine(rc));
                }
            }
        } catch (IOException e) {
            return false;
        }
        return found;
    }


    public synchronized boolean deleteRecurrentEventFromCsv(int eventId) {
        List<RecurrentEvent> allRules = readAllRecurrentEventsFromCsv();
        try (PrintWriter pw = new PrintWriter(new FileWriter(recurrentPath, false))) {
            allRules.stream().filter(rc -> (rc.getEventId() != eventId)).forEachOrdered(rc -> {
                pw.println(recurrentToCsvLine(rc));
            });
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private String eventToCsvLine(Event e) {
        return e.getEventId() + "|" + e.getTitle() + "|" + e.getDescription() + "|" + 
               e.getStartDateTime() + "|" + e.getEndDateTime() + "|" + 
               e.getLocation() + "|" + e.getCategory();
    }

    private String recurrentToCsvLine(RecurrentEvent rc) {
        return rc.getEventId() + "|" + rc.getRecurrentInterval() + "|" + 
               rc.getRecurrentTimes() + "|" + rc.getRecurrentEndDate();
    }


public synchronized void deleteReminderConfigFromCsv(int eventId) {
    List<ReminderConfig> allConfigs = readAllReminderConfigs();
    try (PrintWriter pw = new PrintWriter(new FileWriter(reminderPath, false))) {
        allConfigs.stream().filter(rc -> (rc.getEventId() != eventId)).forEachOrdered(rc -> {
            pw.println(rc.getEventId() + "|" + rc.getRemindDuration() + "|" + rc.isEnable());
        });
    } catch (IOException e) {
        System.err.println("Failed to perform physical deletion of reminder: " + e.getMessage());
    }
}

}





