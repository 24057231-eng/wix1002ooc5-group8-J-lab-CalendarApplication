package calenderApplication.businessLogic;//Define the package

import java.time.LocalDateTime;//Import LocalDateTime class
import java.time.format.DateTimeParseException;//Import exception for date parsing
import java.util.ArrayList;//Import ArrayList
import java.util.Collections;//Import Collections utility
import java.util.List;//Import List interface

public class Event{//Define Event class
    private int eventId;//Store event ID
    private String title;//Store event title
    private String description;//Store event description
    private LocalDateTime startDateTime;//Store start time
    private LocalDateTime endDateTime;//Store end time

    //Additional Event Fields
    private String location;//Store location
    private String category;//Store category
    private List<String>attendees=new ArrayList<>();//Initialize attendees list

    //--- Constructors (spec) ---
    public Event(){}//Default constructor

    public Event(String title,String description,LocalDateTime startDateTime,LocalDateTime endDateTime){//Parameterized constructor
        this.title=title;//Set title
        this.description=description;//Set description
        this.startDateTime=startDateTime;//Set start time
        this.endDateTime=endDateTime;//Set end time
    }//End constructor

    //--- Getters (spec) ---
    public int getEventId(){return eventId;}//Get event ID
    public String getTitle(){return title;}//Get event title
    public String getDescription(){return description;}//Get event description
    public LocalDateTime getStartDateTimeAsLdt(){return startDateTime;}//Get start time object
    public LocalDateTime getEndDateTimeAsLdt(){return endDateTime;}//Get end time object

    //Compatibility getter for existing FileIOManager which concatenates strings
    public String getStartDateTime(){return startDateTime==null?"":startDateTime.toString();}//Get start time string
    public String getEndDateTime(){return endDateTime==null?"":endDateTime.toString();}//Get end time string

    public String getLocation(){return location;}//Get location
    public List<String>getAttendees(){return Collections.unmodifiableList(attendees);}//Get read-only list of attendees
    public String getCategory(){return category;}//Get category

    //--- Setters (spec) ---
    public void setEventId(int eventId){this.eventId=eventId;}//Set event ID
    public void setTitle(String title){this.title=title;}//Set event title
    public void setDescription(String description){this.description=description;}//Set event description
    public void setStartDateTime(LocalDateTime startDateTime){this.startDateTime=startDateTime;}//Set start time object
    public void setEndDateTime(LocalDateTime endDateTime){this.endDateTime=endDateTime;}//Set end time object

    //Compatibility setters used by FileIOManager (String -> LocalDateTime)
    public void setStartDateTime(String iso){this.startDateTime=parseIso(iso);}//Set start time from string
    public void setEndDateTime(String iso){this.endDateTime=parseIso(iso);}//Set end time from string

    public void setLocation(String location){this.location=location;}//Set location
    public void setAttendees(List<String>attendees){//Set attendees list
        this.attendees=(attendees==null)?new ArrayList<>():new ArrayList<>(attendees);//Copy list safely
    }//End setAttendees
    public void setCategory(String category){this.category=category;}//Set category

    //--- Helpers ---
    private LocalDateTime parseIso(String iso){//Helper method to parse date string
        if(iso==null||iso.trim().isEmpty())return null;//Check if string is empty
        try{//Start try block
            return LocalDateTime.parse(iso.trim());//Parse the string
        }catch(DateTimeParseException ex){//Catch parsing error
            //Fallback: treat as null to avoid crashing IO parse
            return null;//Return null on failure
        }//End try-catch
    }//End parseIso

    public boolean isTimeValid(){//Check if time range is valid
        return startDateTime!=null&&endDateTime!=null&&startDateTime.isBefore(endDateTime);//Verify start is before end
    }//End isTimeValid
}//End class