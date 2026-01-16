/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.businessLogic;//Define the package

/**
 *
 * */
public class Reminder{//Define the class
    private int eventId;//Store event ID
    private String reminderTime;//Store reminder time string
    private String message;//Store message content
    
    public Reminder(){//Default constructor
        
    }//End constructor
    public Reminder(int eventId,String reminderTime,String message){//Parameterized constructor
        this.eventId=eventId;//Set event ID
        this.reminderTime=reminderTime;//Set reminder time
        this.message=message;//Set message
    }//End constructor
    public int getEventId(){return eventId;}//Get event ID
    public void setEventId(int eventId){this.eventId=eventId;}//Set event ID

    public String getReminderTime(){return reminderTime;}//Get reminder time
    public void setReminderTime(String reminderTime){this.reminderTime=reminderTime;}//Set reminder time

    public String getMessage(){return message;}//Get message
    public void setMessage(String message){this.message=message;}//Set message
}//End class
