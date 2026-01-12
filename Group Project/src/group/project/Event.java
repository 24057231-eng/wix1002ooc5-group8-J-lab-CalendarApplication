/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package group.project;

public class Event {
    private int event_ID;
    private String title;
    private String description;
    private String start_time;
    private String end_time;
    private String location;
    private String category;
    public Event(){
        
    }
    
    //methods to get
    public int get_event_ID(){
        return event_ID;
    }
    public String get_title(){
        return title;
    }
    public String get_description(){
        return description;
    }
    public String get_start_time(){
        return start_time;
    }
    public String get_end_time(){
        return end_time;
    }
    public String get_location(){
        return location;
    }
    public String get_category(){
        return category;
    }
    
    //methods to set
    public void set_event_ID(int a){
        event_ID=a;
    }
    public void set_title(String a){
        title=a;
    }
    public void set_description(String a){
        description=a;
    }
    public void set_start_time(String a){
        start_time=a;
    }
    public void set_end_time(String a){
        end_time=a;
    }
    public void set_location(String a){
        location=a;
    }
    public void set_category(String a){
        category=a;
    }
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
