package calenderApplication.businessLogic;//Define the package

import calenderApplication.dataLayer.FileIOManager;//Import file manager
import java.time.Duration;//Import Duration class
import java.time.LocalDateTime;//Import LocalDateTime class
import java.util.*;//Import utility classes

public class ReminderManager{//Define the class
    private final EventManager eventManager;//Declare event manager
    private final FileIOManager ioManager;//Declare IO manager
    private final Map<Integer,ReminderConfig>reminderByEventId=new HashMap<>();//Create memory map for reminders

    public ReminderManager(EventManager eventManager,FileIOManager ioManager){//Constructor
        this.eventManager=eventManager;//Assign event manager
        this.ioManager=ioManager;//Assign IO manager

        //load from reminder.csv; if duplicates exist, last one wins
        for(ReminderConfig c:ioManager.readAllReminderConfigs()){//Loop through loaded configs
            reminderByEventId.put(c.getEventId(),c);//Store in map
        }//End loop
    }//End constructor

    public void setReminder(ReminderConfig config){//Method to set reminder
        if(config==null||config.getEventId()<=0)return;//Check validity
        if(config.getRemindDuration()==null){//Check if duration is null
            config.setRemindDuration(Duration.ofMinutes(30));//Set default duration
        }//End if

        reminderByEventId.put(config.getEventId(),config);//Update memory map

        //persistent append (your FileIOManager writes append)
        ioManager.writeReminderConfigToFile(config);//Save to file
    }//End setReminder

    public void disableReminder(int eventId){//Method to disable reminder
        if(eventId<=0)return;//Check ID validity
        ReminderConfig c=reminderByEventId.get(eventId);//Get config from map
        if(c!=null){//If config exists
            c.setEnable(false);//Disable it
            setReminder(c);//Save changes
        }//End if
    }//End disableReminder

    public List<String>getUpcomingReminders(){//Method to get alerts
        LocalDateTime now=LocalDateTime.now();//Get current time
        List<String>res=new ArrayList<>();//Create result list

        for(Event e:eventManager.getAllBaseEvents()){//Loop through events
            ReminderConfig cfg=reminderByEventId.get(e.getEventId());//Get reminder config
            if(cfg==null||!cfg.isEnable())continue;//Skip if invalid or disabled
            if(e.getStartDateTimeAsLdt()==null)continue;//Skip if no start time

            Duration d=cfg.getRemindDurationAsDuration();//Get duration
            LocalDateTime remindAt=e.getStartDateTimeAsLdt().minus(d);//Calculate trigger time

            if((now.isAfter(remindAt)||now.equals(remindAt))&&now.isBefore(e.getStartDateTimeAsLdt())){//Check time window
                long mins=Math.max(0,Duration.between(now,e.getStartDateTimeAsLdt()).toMinutes());//Calculate remaining minutes
                res.add("Your next event is coming soon in "+mins+" minutes: "+e.getTitle());//Add message
            }//End time check
        }//End loop

        return res;//Return list
    }//End getUpcomingReminders

    public ReminderConfig getReminderConfig(int eventId){//Method to get config
        return reminderByEventId.get(eventId);//Return config from map
    }//End getReminderConfig
    
public void deleteReminder(int eventId){//Method to delete reminder
    if(eventId<=0)return;//Check ID validity

    reminderByEventId.remove(eventId);//Remove from memory

    ioManager.deleteReminderConfigFromCsv(eventId);//Delete from file
}//End deleteReminder
}//End class