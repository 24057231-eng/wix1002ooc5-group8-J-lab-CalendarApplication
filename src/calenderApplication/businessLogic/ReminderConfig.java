package calenderApplication.businessLogic;//Define the package

import java.time.Duration;//Import Duration class

public class ReminderConfig{//Define the class
    private int eventId;//Store event ID
    private Duration remindDuration;//Store duration
    private boolean enable;//Store status

    public ReminderConfig(){//Default constructor
        this.remindDuration=Duration.ofMinutes(30);//Set default duration
        this.enable=false;//Set default status
    }//End constructor

    public ReminderConfig(int eventId,Duration remindDuration,boolean enable){//Parameterized constructor
        this.eventId=eventId;//Set event ID
        this.remindDuration=remindDuration;//Set duration
        this.enable=enable;//Set status
    }//End constructor

    public int getEventId(){return eventId;}//Get event ID
    public Duration getRemindDuration(){return remindDuration!=null?remindDuration:Duration.ofMinutes(30);}//Get duration with safe check
    public boolean isEnable(){return enable;}//Check if enabled

    public void setEventId(int eventId){this.eventId=eventId;}//Set event ID
    public void setRemindDuration(Duration remindDuration){this.remindDuration=remindDuration;}//Set duration
    public void setEnable(boolean enable){this.enable=enable;}//Set status

    public Duration getRemindDurationAsDuration(){//Method to get duration
    if(remindDuration==null){//Check if null
        return Duration.ofMinutes(30);//Return default
    }//End if
        return remindDuration;//Return actual duration
    }//End method
}//End class
