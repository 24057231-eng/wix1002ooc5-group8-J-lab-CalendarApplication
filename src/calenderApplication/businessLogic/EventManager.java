package calenderApplication.businessLogic;//Define the package

import calenderApplication.dataLayer.EventIdGenerator;//Import ID generator
import calenderApplication.dataLayer.FileIOManager;//Import file manager
import java.time.LocalDate;//Import LocalDate
import java.time.LocalDateTime;//Import LocalDateTime
import java.util.*;//Import utility classes
import java.util.stream.Collectors;//Import Collectors

public class EventManager{//Define the EventManager class
    private final FileIOManager ioManager;//Declare IO manager
    private ReminderManager reminderManager;//Declare reminder manager
    
    //Memory Cache: Key is EventID
    private final Map<Integer,Event>eventCache=new HashMap<>();//Create event cache map
    private final Map<Integer,RecurrentEvent>recurrentRulesByEventId=new HashMap<>();//Create recurrence rule map

    public EventManager(FileIOManager ioManager){//Constructor
        this.ioManager=ioManager;//Assign IO manager
        loadDataIntoMemory();//Load data from file
    }//End constructor
    
    private void loadDataIntoMemory(){//Method to load data
        eventCache.clear();//Clear event cache
        ioManager.readAllEventsFromCsv().forEach(e->eventCache.put(e.getEventId(),e));//Load events to cache

        recurrentRulesByEventId.clear();//Clear recurrence cache
        ioManager.readAllRecurrentEventsFromCsv().forEach(r->{//Load recurrence rules
            recurrentRulesByEventId.put(r.getEventId(),r);//Put rule in map
        });//End forEach
    }//End loadDataIntoMemory

public boolean createEvent(Event event,RecurrentEvent recurrentEvent){//Method to create event
        if(!isEventValidForCreate(event))return false;//Check if event is valid

        //Conflict check (based on memory)
        if(!checkEventConflict(event).isEmpty())return false;//Check for conflicts

        //Assign ID and write to file
        int newId=EventIdGenerator.generateNextEventId();//Generate new ID
        event.setEventId(newId);//Set ID to event
        ioManager.writeEventToCsv(event);//Write event to file
        
        //Update memory cache
        eventCache.put(newId,event);//Add to cache

        if(recurrentEvent!=null&&recurrentEvent.isEnabled()){//Check if recurrence exists
            recurrentEvent.setEventId(newId);//Set ID to recurrence
            ioManager.writeRecurrentEventToCsv(recurrentEvent);//Write recurrence to file
            recurrentRulesByEventId.put(newId,recurrentEvent);//Add to recurrence cache
        }//End if
        return true;//Return success
    }//End createEvent

public boolean updateEvent(Event event,RecurrentEvent recurrent){//Method to update event
        if(!isEventValidForCreate(event))return false;//Check validity

        if(ioManager.updateEventInCsv(event)){//Try to update CSV
            eventCache.put(event.getEventId(),event);//Refresh memory
            
            if(recurrent!=null){//Check if recurrence exists
                recurrent.setEventId(event.getEventId());//Set ID
                ioManager.updateRecurrentEventInCsv(recurrent);//Update recurrence CSV
                recurrentRulesByEventId.put(event.getEventId(),recurrent);//Update memory
            }//End if
            return true;//Return success
        }//End if
        return false;//Return failure
    }//End updateEvent

public boolean deleteEvent(int eventId){//Method to delete event
        boolean deleted=ioManager.deleteEventFromCsv(eventId);//Delete from CSV
        if(deleted){//If delete successful
            //Synchronize cleaning memory
            eventCache.remove(eventId);//Remove from cache
            recurrentRulesByEventId.remove(eventId);//Remove recurrence rule
            ioManager.deleteRecurrentEventFromCsv(eventId);//Delete recurrence from CSV
            
            if(this.reminderManager!=null){//Check if reminder manager exists
                this.reminderManager.deleteReminder(eventId);//Delete reminder
            }//End if
            return true;//Return success
        }//End if
        return false;//Return failure
    }//End deleteEvent

    public List<Event>getEventsForDate(LocalDate date){//Method to get events by date
        return getAllEventsExpanded().stream()//Stream all events
            .filter(e->e.getStartDateTimeAsLdt()!=null&&//Check if start time exists
                         e.getStartDateTimeAsLdt().toLocalDate().equals(date))//Check if date matches
            .collect(Collectors.toList());//Collect to list
    }//End getEventsForDate
    
    public List<Event>getAllEventsExpanded(){//Method to get all events
        List<Event>expanded=new ArrayList<>(eventCache.values());//Get base events
        for(Event base:eventCache.values()){//Loop through base events
            RecurrentEvent rule=recurrentRulesByEventId.get(base.getEventId());//Get rule
            if(rule!=null&&rule.isEnabled()){//Check if rule is active
                expanded.addAll(generateRecurrentEvents(base,rule));//Generate and add events
            }//End if
        }//End loop
        return expanded;//Return list
    }//End getAllEventsExpanded

    //--- recurrent generation (end <= endDate) ---
private List<Event>generateRecurrentEvents(Event base,RecurrentEvent rule){//Method to generate events
        List<Event>results=new ArrayList<>();//Create result list
        int days=parseIntervalToDays(rule.getRecurrentInterval());//Parse interval
        if(days<=0)return results;//Return empty if invalid

        //Start from 1, because 0 is the base event itself
        for(int i=1;i<rule.getRecurrentTimes();i++){//Loop times
            results.add(cloneWithShift(base,i*days));//Add cloned event
        }//End loop
        return results;//Return list
    }//End generateRecurrentEvents

public List<Event>checkEventConflict(Event newEvent){//Method to check conflicts
        LocalDateTime newStart=newEvent.getStartDateTimeAsLdt();//Get new start time
        LocalDateTime newEnd=newEvent.getEndDateTimeAsLdt();//Get new end time
        if(newStart==null||newEnd==null)return Collections.emptyList();//Return empty if null

        return eventCache.values().stream()//Stream existing events
            .filter(ex->{//Filter logic
                //Exclude the event itself being edited
                if(ex.getEventId()==newEvent.getEventId())return false;//Skip self
                LocalDateTime exStart=ex.getStartDateTimeAsLdt();//Get existing start
                LocalDateTime exEnd=ex.getEndDateTimeAsLdt();//Get existing end
                return exStart!=null&&exEnd!=null&&//Check not null
                       newStart.isBefore(exEnd)&&exStart.isBefore(newEnd);//Check overlap
            })//End filter
            .collect(Collectors.toList());//Collect to list
    }//End checkEventConflict

    public Collection<Event>getAllBaseEvents(){//Get all base events
        return eventCache.values();//Return cache values
    }//End getAllBaseEvents

    public RecurrentEvent getRecurrentRule(int eventId){//Get recurrence rule
        return recurrentRulesByEventId.get(eventId);//Return rule
    }//End getRecurrentRule

    //--- Auxiliary private methods ---
    
    private int parseIntervalToDays(String interval){//Helper to parse days
        if(interval==null)return 0;//Return 0 if null
        switch(interval.toLowerCase()){//Switch on interval string
            case"1d":return 1;//Return 1 day
            case"1w":return 7;//Return 1 week
            case"2w":return 14;//Return 2 weeks
            case"4w":return 28;//Return 4 weeks
            default:return 0;//Return 0
        }//End switch
    }//End parseIntervalToDays
    
    private Event cloneWithShift(Event base,int shiftDays){//Helper to clone event
        Event e=new Event();//Create new event
        e.setEventId(base.getEventId());//Copy ID
        e.setTitle(base.getTitle()+" (R)");//Mark as repeated
        e.setDescription(base.getDescription());//Copy description
        e.setLocation(base.getLocation());//Copy location
        e.setCategory(base.getCategory());//Copy category
        e.setAttendees(new ArrayList<>(base.getAttendees()));//Copy attendees

        if(base.getStartDateTimeAsLdt()!=null)//Check start time
            e.setStartDateTime(base.getStartDateTimeAsLdt().plusDays(shiftDays));//Shift start time
        if(base.getEndDateTimeAsLdt()!=null)//Check end time
            e.setEndDateTime(base.getEndDateTimeAsLdt().plusDays(shiftDays));//Shift end time
        return e;//Return new event
    }//End cloneWithShift

    private boolean isEventValidForCreate(Event e){//Helper to validate event
        return e!=null&&e.getTitle()!=null&&!e.getTitle().trim().isEmpty()//Check title
                &&e.getStartDateTimeAsLdt()!=null&&e.getEndDateTimeAsLdt()!=null//Check dates
                &&e.getEndDateTimeAsLdt().isAfter(e.getStartDateTimeAsLdt());//Check order
    }//End isEventValidForCreate

    public void setReminderManager(ReminderManager rm){//Setter for reminder manager
        this.reminderManager=rm;//Assign manager
    }//End setReminderManager
    
}//End class