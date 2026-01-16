package calenderApplication.businessLogic;//Define the package

import java.time.LocalDate;//Import LocalDate class

public class RecurrentEvent{//Define the class
    private int eventId;//Store event ID
    private String recurrentInterval;//Store recurrence interval string
    private int recurrentTimes;//Store number of repetitions
    private String recurrentEndDate;//Store end date as string

    public RecurrentEvent(){}//Default constructor

    public RecurrentEvent(int eventId,String recurrentInterval,int recurrentTimes,String recurrentEndDate){//Parameterized constructor
        this.eventId=eventId;//Set event ID
        this.recurrentInterval=recurrentInterval;//Set interval
        this.recurrentTimes=recurrentTimes;//Set times
        this.recurrentEndDate=recurrentEndDate;//Set end date
    }//End constructor

    public int getEventId(){return eventId;}//Get event ID
    public String getRecurrentInterval(){return recurrentInterval;}//Get recurrence interval
    public int getRecurrentTimes(){return recurrentTimes;}//Get recurrence times
    public String getRecurrentEndDate(){return recurrentEndDate;}//Get end date string

    public void setEventId(int eventId){this.eventId=eventId;}//Set event ID
    public void setRecurrentInterval(String recurrentInterval){this.recurrentInterval=recurrentInterval;}//Set interval
    public void setRecurrentTimes(int recurrentTimes){this.recurrentTimes=recurrentTimes;}//Set times
    public void setRecurrentEndDate(String recurrentEndDate){this.recurrentEndDate=recurrentEndDate;}//Set end date string

    /**
     * Compatibility setter for FileIOManager.java which may pass a LocalDate when reading CSV.
     * We still store it as an ISO-8601 String (yyyy-MM-dd) to keep write/concat logic stable.
     */
    public void setRecurrentEndDate(LocalDate recurrentEndDate){//Set end date from object
        this.recurrentEndDate=(recurrentEndDate==null)?null:recurrentEndDate.toString();//Convert date to string
    }//End method
    
    /** Business helper: parse endDate string to LocalDate, return null if not usable */
    public LocalDate getRecurrentEndDateAsLocalDate(){//Get date object
        if(recurrentEndDate==null)return null;//Check if null
        String s=recurrentEndDate.trim();//Trim whitespace
        if(s.isEmpty()||s.equalsIgnoreCase("null"))return null;//Check validity
        try{//Start try block
            return LocalDate.parse(s);//Parse string to date
        }catch(Exception e){//Catch parsing error
            return null;//Return null on error
        }//End try-catch
    }//End method

private boolean enabled;//Store enabled status

public void setEnabled(boolean b){//Set enabled status
    this.enabled=b;//Update status
}//End method

public boolean isEnabled(){//Check if enabled
    return enabled&&recurrentInterval!=null&&!recurrentInterval.trim().isEmpty();//Verify logic
}//End method
}//End class
