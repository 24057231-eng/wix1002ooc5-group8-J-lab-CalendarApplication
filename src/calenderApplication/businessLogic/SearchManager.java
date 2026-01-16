package calenderApplication.businessLogic;//Define the package

import java.time.LocalDate;//Import LocalDate
import java.util.ArrayList;//Import ArrayList
import java.util.Comparator;//Import Comparator
import java.util.List;//Import List

public class SearchManager{//Define the class
    private final EventManager eventManager;//Declare event manager

    public SearchManager(EventManager eventManager){//Constructor
        this.eventManager=eventManager;//Assign event manager
    }//End constructor

    public List<Event>searchEventsByDate(LocalDate targetDate){//Method to search by date
        List<Event>all=eventManager.getAllEventsExpanded();//Get all events
        List<Event>res=new ArrayList<>();//Create result list
        for(Event e:all){//Loop through events
            if(e.getStartDateTimeAsLdt()==null)continue;//Skip if null
            if(e.getStartDateTimeAsLdt().toLocalDate().equals(targetDate))res.add(e);//Add matching event
        }//End loop
        res.sort(Comparator.comparing(Event::getStartDateTimeAsLdt,Comparator.nullsLast(Comparator.naturalOrder())));//Sort results
        return res;//Return list
    }//End method

    public List<Event>searchEventsByDateRange(LocalDate startDate,LocalDate endDate){//Method to search range
        List<Event>all=eventManager.getAllEventsExpanded();//Get all events
        List<Event>res=new ArrayList<>();//Create result list
        for(Event e:all){//Loop through events
            if(e.getStartDateTimeAsLdt()==null)continue;//Skip if null
            LocalDate d=e.getStartDateTimeAsLdt().toLocalDate();//Get event date
            if((d.isAfter(startDate)||d.equals(startDate))&&(d.isBefore(endDate)||d.equals(endDate))){//Check range
                res.add(e);//Add matching event
            }//End if
        }//End loop
        res.sort(Comparator.comparing(Event::getStartDateTimeAsLdt,Comparator.nullsLast(Comparator.naturalOrder())));//Sort results
        return res;//Return list
    }//End method

    public List<Event>searchEventsByTitle(String keyword){//Method to search title
        String k=(keyword==null)?"":keyword.trim().toLowerCase();//Process keyword
        List<Event>all=eventManager.getAllEventsExpanded();//Get all events
        List<Event>res=new ArrayList<>();//Create result list
        for(Event e:all){//Loop through events
            String t=(e.getTitle()==null)?"":e.getTitle().toLowerCase();//Get title
            if(t.contains(k))res.add(e);//Check match
        }//End loop
        return res;//Return list
    }//End method

    public List<Event>filterEventsByCategory(String category){//Method to filter category
        String k=(category==null)?"":category.trim().toLowerCase();//Process category
        List<Event>all=eventManager.getAllEventsExpanded();//Get all events
        List<Event>res=new ArrayList<>();//Create result list
        for(Event e:all){//Loop through events
            String c=(e.getCategory()==null)?"":e.getCategory().toLowerCase();//Get category
            if(c.equals(k))res.add(e);//Check match
        }//End loop
        return res;//Return list
    }//End method

    public List<Event>filterEventsByLocation(String location){//Method to filter location
        String k=(location==null)?"":location.trim().toLowerCase();//Process location
        List<Event>all=eventManager.getAllEventsExpanded();//Get all events
        List<Event>res=new ArrayList<>();//Create result list
        for(Event e:all){//Loop through events
            String loc=(e.getLocation()==null)?"":e.getLocation().toLowerCase();//Get location
            if(loc.equals(k))res.add(e);//Check match
        }//End loop
        return res;//Return list
    }//End method
}//End class