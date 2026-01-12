/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
public class Reminder {
    private int eventId;
    private String time;
    private String message;
    
    public Reminder(){
        
    }
    
    public int getEventId(){
        return eventId;
    }
    public String getTime(){
        return time;
    }
    public String getMessage(){
        return message;
    }
    
    public void setEventId(int eventId){
        this.eventId = eventId;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setMessage(String message){
        this.message = message;
    }
}