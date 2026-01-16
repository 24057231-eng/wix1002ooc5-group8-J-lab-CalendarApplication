package calenderApplication.businessLogic;//Define the package

import java.time.DayOfWeek;//Import DayOfWeek
import java.time.Duration;//Import Duration
import java.time.LocalDate;//Import LocalDate
import java.util.*;//Import utilities

public class StatisticManager{//Define the class
    private final EventManager eventManager;//Declare event manager

    public StatisticManager(EventManager eventManager){//Constructor
        this.eventManager=eventManager;//Assign event manager
    }//End constructor

    public DayOfWeek getBusiestDayInWeek(){//Method to find busiest day
        Map<DayOfWeek,Integer>cnt=new EnumMap<>(DayOfWeek.class);//Create count map
        for(DayOfWeek d:DayOfWeek.values())cnt.put(d,0);//Initialize map

        for(Event e:eventManager.getAllEventsExpanded()){//Loop through events
            if(e.getStartDateTimeAsLdt()==null)continue;//Skip if date is null
            DayOfWeek d=e.getStartDateTimeAsLdt().getDayOfWeek();//Get day of week
            cnt.put(d,cnt.get(d)+1);//Increment count
        }//End loop

        DayOfWeek best=DayOfWeek.MONDAY;//Initialize best day
        int bestN=-1;//Initialize max count
        for(DayOfWeek d:DayOfWeek.values()){//Loop days
            int n=cnt.get(d);//Get count
            if(n>bestN){bestN=n;best=d;}//Update max if higher
        }//End loop
        return best;//Return result
    }//End method

    public Map<String,Integer>getEventCategoryDistribution(){//Method for category stats
        Map<String,Integer>map=new HashMap<>();//Create map
        for(Event e:eventManager.getAllEventsExpanded()){//Loop through events
            String c=(e.getCategory()==null||e.getCategory().trim().isEmpty())?"Uncategorized":e.getCategory().trim();//Get category name
            map.put(c,map.getOrDefault(c,0)+1);//Update count
        }//End loop
        return map;//Return map
    }//End method

    public int getMonthlyEventCount(LocalDate month){//Method for monthly count
        if(month==null)return 0;//Return 0 if null
        int y=month.getYear();//Get year
        int m=month.getMonthValue();//Get month

        int count=0;//Initialize count
        for(Event e:eventManager.getAllEventsExpanded()){//Loop through events
            if(e.getStartDateTimeAsLdt()==null)continue;//Skip if null
            if(e.getStartDateTimeAsLdt().getYear()==y&&e.getStartDateTimeAsLdt().getMonthValue()==m){//Check match
                count++;//Increment count
            }//End if
        }//End loop
        return count;//Return count
    }//End method

    public double getAverageEventDuration(){//Method for average duration
        long totalMinutes=0;//Initialize total
        int n=0;//Initialize count

        for(Event e:eventManager.getAllEventsExpanded()){//Loop through events
            if(e.getStartDateTimeAsLdt()==null||e.getEndDateTimeAsLdt()==null)continue;//Skip if null
            long mins=Duration.between(e.getStartDateTimeAsLdt(),e.getEndDateTimeAsLdt()).toMinutes();//Calculate minutes
            if(mins>0){//Check if positive
                totalMinutes+=mins;//Add to total
                n++;//Increment count
            }//End if
        }//End loop
        return(n==0)?0.0:(double)totalMinutes/n;//Return average
    }//End method
}//End class