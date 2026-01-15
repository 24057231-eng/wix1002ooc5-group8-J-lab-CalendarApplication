/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
public class Event {
    private int eventId;
    private String title;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private String location;
    private String category;
    
    public Event(){
        
    }
    
    // Methods to get
    public int getEventId(){
        return eventId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getStartDateTime(){
        return startDateTime;
    }

    public String getEndDateTime(){
        return endDateTime;
    }

    public String getLocation(){
        return location;
    }

    public String getCategory(){
        return category;
    }
    
    // Methods to set
    public void setEventId(int eventId){
        this.eventId = eventId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setStartDateTime(String startDateTime){
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime){
        this.endDateTime = endDateTime;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setCategory(String category){
        this.category = category;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
}