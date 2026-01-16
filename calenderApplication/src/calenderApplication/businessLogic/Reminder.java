/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.businessLogic;

/**
 *
 * @author 星飞
 */
public class Reminder {
    private int eventId;
    private String reminderTime;
    private String message;
    
    public Reminder(){
        
    }
    public Reminder(int eventId, String reminderTime, String message) {
        this.eventId = eventId;
        this.reminderTime = reminderTime;
        this.message = message;
    }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
