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
    private int event_ID;
    private String time;
    private String message;
    
    public Reminder(){
        
    }
    
    public int get_event_ID(){
        return event_ID;
    }
    public String get_time(){
        return time;
    }
    public String get_message(){
        return message;
    }
    
    public void set_event_ID(int a){
        event_ID=a;
    }
    public void set_time(String a){
        time=a;
    }
    public void set_message(String a){
        message=a;
    }
}
