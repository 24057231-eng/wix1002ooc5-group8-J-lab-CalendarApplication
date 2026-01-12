/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
public class ReminderConfig {
    private int eventId;
    private String remindDuration;
    private boolean enable;
    
    public ReminderConfig(){
        
    }
    
    public int getEventId(){
        return eventId;
    }
    public String getRemindDuration(){
        return remindDuration;
    }
    public boolean isEnable(){
        return enable;
    }
    
    public void setEventId(int eventId){
        this.eventId=eventId;
    }
    public void setRemindDuration(String remindDuration){
        this.remindDuration=remindDuration;
    }
    public void setEnable(boolean enable){
        this.enable=enable;
    }
}
