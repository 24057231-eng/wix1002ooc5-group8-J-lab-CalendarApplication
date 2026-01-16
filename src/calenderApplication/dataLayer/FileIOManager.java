/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.dataLayer;//Define the package

import calenderApplication.businessLogic.Event;//Import Event class
import calenderApplication.businessLogic.RecurrentEvent;//Import RecurrentEvent class
import calenderApplication.businessLogic.ReminderConfig;//Import ReminderConfig class
import java.io.*;//Import IO classes
import java.util.ArrayList;//Import ArrayList
import java.util.List;//Import List interface
import java.util.Scanner;//Import Scanner
import java.time.Duration;//Import Duration
import java.time.LocalDateTime;//Import LocalDateTime

/**
 *
 */
public class FileIOManager{//Define the class
    private final String eventPath="event.csv";//Define event file path
    private final String recurrentPath="recurrent.csv";//Define recurrent file path
    private final String reminderPath="reminder.csv";//Define reminder file path

    //Event
    public synchronized void writeEventToCsv(Event event){//Method to write event
        //ID|Title|Description|StartTime|EndTime|Location|Category
        String line=event.getEventId()+"|"+//Start building string with ID
                      event.getTitle()+"|"+//Add title
                      event.getDescription()+"|"+//Add description
                      event.getStartDateTime()+"|"+//Add start time
                      event.getEndDateTime()+"|"+//Add end time
                      event.getLocation()+"|"+//Add location
                      event.getCategory();//Add category
        writeLineToFile(eventPath,line);//Write line to file
    }//End writeEventToCsv

    public List<Event>readAllEventsFromCsv(){//Method to read events
        List<Event>list=new ArrayList<>();//Create empty list
        File f=new File(eventPath);//Create file object
        if(!f.exists())return list;//Return empty if file missing

        try(Scanner s=new Scanner(f)){//Try to read file
            while(s.hasNextLine()){//Loop through lines
                String line=s.nextLine().trim();//Read and trim line
                if(line.isEmpty())continue;//Skip empty lines
                String[]p=line.split("\\|");//Split by pipe
                if(p.length>=7){//Check if enough parts
                    Event ev=new Event();//Create new event
                    ev.setEventId(Integer.parseInt(p[0]));//Set ID
                    ev.setTitle(p[1]);//Set title
                    ev.setDescription(p[2]);//Set description
                    ev.setStartDateTime(LocalDateTime.parse(p[3]));//Set start time
                    ev.setEndDateTime(LocalDateTime.parse(p[4]));//Set end time
                    ev.setLocation(p[5]);//Set location
                    ev.setCategory(p[6]);//Set category
                    list.add(ev);//Add to list
                }//End if
            }//End while
        }catch(Exception e){//Catch errors
            System.err.println("Error reading events: "+e.getMessage());//Print error
        }//End try-catch
        return list;//Return list
    }//End readAllEventsFromCsv

    //RecurrentEvent
    public synchronized void writeRecurrentEventToCsv(RecurrentEvent rc){//Method to write recurrent event
        String line=rc.getEventId()+"|"+//Start string with ID
                      rc.getRecurrentInterval()+"|"+//Add interval
                      rc.getRecurrentTimes()+"|"+//Add times
                      rc.getRecurrentEndDate();//Add end date
        writeLineToFile(recurrentPath,line);//Write to file
    }//End writeRecurrentEventToCsv

    public List<RecurrentEvent>readAllRecurrentEventsFromCsv(){//Method to read recurrent events
        List<RecurrentEvent>list=new ArrayList<>();//Create empty list
        File f=new File(recurrentPath);//Create file object
        if(!f.exists())return list;//Return empty if missing

        try(Scanner s=new Scanner(f)){//Try to scan file
            while(s.hasNextLine()){//Loop through lines
                String line=s.nextLine().trim();//Read line
                if(line.isEmpty())continue;//Skip empty
                String[]p=line.split("\\|");//Split string
                if(p.length>=4){//Check length
                    list.add(new RecurrentEvent(//Add new object
                        Integer.parseInt(p[0]),p[1],Integer.parseInt(p[2]),p[3]//Parse and create
                    ));//End add
                }//End if
            }//End while
        }catch(Exception e){//Catch error
            System.err.println("Error reading recurrences: "+e.getMessage());//Print error
        }//End try-catch
        return list;//Return list
    }//End readAllRecurrentEventsFromCsv

    //ReminderConfig
    public synchronized void writeReminderConfigToFile(ReminderConfig rm){//Method to write reminder
        String line=rm.getEventId()+"|"+//Start string with ID
                      rm.getRemindDuration()+"|"+//Add duration
                      rm.isEnable();//Add enable status
        writeLineToFile(reminderPath,line);//Write to file
    }//End writeReminderConfigToFile

public List<ReminderConfig>readAllReminderConfigs(){//Method to read reminders
        List<ReminderConfig>list=new ArrayList<>();//Create list
        File f=new File(reminderPath);//Create file object
        if(!f.exists())return list;//Return empty if missing

        try(Scanner s=new Scanner(f)){//Try to scan file
            while(s.hasNextLine()){//Loop lines
                String line=s.nextLine().trim();//Read line
                if(line.isEmpty())continue;//Skip empty
                String[]p=line.split("\\|");//Split string
                if(p.length>=3){//Check length
                    list.add(new ReminderConfig(//Add object
                        Integer.parseInt(p[0]),//Parse ID
                        Duration.parse(p[1]),//Parse duration
                        Boolean.parseBoolean(p[2])//Parse boolean
                    ));//End add
                }//End if
            }//End while
        }catch(Exception e){//Catch error
            System.err.println("Error reading reminders: "+e.getMessage());//Print error
        }//End try-catch
        return list;//Return list
    }//End readAllReminderConfigs

    private void writeLineToFile(String filePath,String line){//Helper to write line
        try(PrintWriter pw=new PrintWriter(new FileWriter(filePath,true))){//Open file in append mode
            pw.println(line);//Print line
        }catch(IOException e){//Catch IO error
            System.err.println("IO Error on "+filePath+": "+e.getMessage());//Print error
        }//End try-catch
    }//End writeLineToFile

    public synchronized boolean updateEventInCsv(Event updatedEvent){//Method to update event
        List<Event>allEvents=readAllEventsFromCsv();//Read all events
        boolean found=false;//Flag for found
        
        try(PrintWriter pw=new PrintWriter(new FileWriter(eventPath,false))){//False means overwrite file
            for(Event e:allEvents){//Loop events
                if(e.getEventId()==updatedEvent.getEventId()){//Check ID match
                    pw.println(eventToCsvLine(updatedEvent));//Write updated data
                    found=true;//Set found true
                }else{//If not match
                    pw.println(eventToCsvLine(e));//Write old data
                }//End else
            }//End for
        }catch(IOException e){//Catch error
            System.err.println("Update Event Error: "+e.getMessage());//Print error
            return false;//Return false
        }//End try-catch
        return found;//Return result
    }//End updateEventInCsv

    public synchronized boolean deleteEventFromCsv(int eventId){//Method to delete event
        List<Event>allEvents=readAllEventsFromCsv();//Read all events
        try(PrintWriter pw=new PrintWriter(new FileWriter(eventPath,false))){//Open file overwrite
            for(Event e:allEvents){//Loop events
                if(e.getEventId()!=eventId){//Check if ID matches
                    pw.println(eventToCsvLine(e));//Write if not deleted
                }//End if
            }//End for
            return true;//Return success
        }catch(IOException e){//Catch error
            System.err.println("Delete Event Error: "+e.getMessage());//Print error
            return false;//Return failure
        }//End try-catch
    }//End deleteEventFromCsv


    public synchronized boolean updateRecurrentEventInCsv(RecurrentEvent updatedRc){//Method to update recurrence
        List<RecurrentEvent>allRules=readAllRecurrentEventsFromCsv();//Read all rules
        boolean found=false;//Flag found
        try(PrintWriter pw=new PrintWriter(new FileWriter(recurrentPath,false))){//Open overwrite
            for(RecurrentEvent rc:allRules){//Loop rules
                if(rc.getEventId()==updatedRc.getEventId()){//Check match
                    pw.println(recurrentToCsvLine(updatedRc));//Write updated
                    found=true;//Set found
                }else{//If not match
                    pw.println(recurrentToCsvLine(rc));//Write old
                }//End else
            }//End for
        }catch(IOException e){//Catch error
            return false;//Return failure
        }//End try-catch
        return found;//Return result
    }//End updateRecurrentEventInCsv


    public synchronized boolean deleteRecurrentEventFromCsv(int eventId){//Method to delete recurrence
        List<RecurrentEvent>allRules=readAllRecurrentEventsFromCsv();//Read all rules
        try(PrintWriter pw=new PrintWriter(new FileWriter(recurrentPath,false))){//Open overwrite
            allRules.stream().filter(rc->(rc.getEventId()!=eventId)).forEachOrdered(rc->{//Filter and loop
                pw.println(recurrentToCsvLine(rc));//Write to file
            });//End stream
            return true;//Return success
        }catch(IOException e){//Catch error
            return false;//Return failure
        }//End try-catch
    }//End deleteRecurrentEventFromCsv


    private String eventToCsvLine(Event e){//Helper to format event
        return e.getEventId()+"|"+e.getTitle()+"|"+e.getDescription()+"|"+//Build string
               e.getStartDateTime()+"|"+e.getEndDateTime()+"|"+//Continue building
               e.getLocation()+"|"+e.getCategory();//Finish building
    }//End eventToCsvLine

    private String recurrentToCsvLine(RecurrentEvent rc){//Helper to format recurrence
        return rc.getEventId()+"|"+rc.getRecurrentInterval()+"|"+//Build string
               rc.getRecurrentTimes()+"|"+rc.getRecurrentEndDate();//Finish building
    }//End recurrentToCsvLine


public synchronized void deleteReminderConfigFromCsv(int eventId){//Method to delete reminder
    List<ReminderConfig>allConfigs=readAllReminderConfigs();//Read all configs
    try(PrintWriter pw=new PrintWriter(new FileWriter(reminderPath,false))){//Open overwrite
        allConfigs.stream().filter(rc->(rc.getEventId()!=eventId)).forEachOrdered(rc->{//Filter and loop
            pw.println(rc.getEventId()+"|"+rc.getRemindDuration()+"|"+rc.isEnable());//Write to file
        });//End stream
    }catch(IOException e){//Catch error
        System.err.println("Failed to perform physical deletion of reminder: "+e.getMessage());//Print error
    }//End try-catch
}//End deleteReminderConfigFromCsv
}//End class