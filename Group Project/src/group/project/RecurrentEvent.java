/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
public class RecurrentEvent {
    private int eventId;
    private String recurrentInterval;
    private int recurrentTimes;
    private String recurrentEndDate;
    
    public RecurrentEvent(){
        
    }
    
    public int getEventId(){
        return eventId;
    }
    public String getRecurrentInterval(){
        return recurrentInterval;
    }
    public int getRecurrentTimes(){
        return recurrentTimes;
    }
    public String getRecurrentEndDate(){
        return recurrentEndDate;
    }
    
    public void setEventId(int eventId){
        this.eventId=eventId;
    }
    public void setRecurrentInterval(String recurrentInterval){
        this.recurrentInterval=recurrentInterval;
    }
    public void setRecurrentTimes(int recurrentTimes){
        this.recurrentTimes=recurrentTimes;
    }
    public void setRecurrentEndDate(String recurrentEndDate){
        this.recurrentEndDate=recurrentEndDate;
    }
}
