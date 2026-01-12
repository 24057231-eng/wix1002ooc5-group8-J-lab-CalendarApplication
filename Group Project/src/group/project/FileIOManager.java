/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileIOManager{
    private final String eventPath="event.csv";
    private final String recurrentPath="recurrent.csv";
    private final String reminderPath="reminder.csv";

    public synchronized void writeEventToCsv(Event event){
        String line=event.getEventId()+"|"+event.getTitle()+"|"+event.getDescription()+"|"+event.getStartDateTime()+"|"+event.getEndDateTime()+"|"+event.getLocation()+"|"+event.getCategory();
        try(PrintWriter pw=new PrintWriter(new FileWriter(eventPath,true))){
            pw.println(line);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public List<Event> readAllEventsFromCsv(){
        List<Event> list=new ArrayList<>();
        File f=new File(eventPath);
        if(!f.exists())return list;

        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine().trim();
                if(line.isEmpty())continue;
                String[] p=line.split("\\|");
                if(p.length>=7){
                    Event e=new Event();
                    e.setEventId(Integer.parseInt(p[0]));
                    e.setTitle(p[1]);
                    e.setDescription(p[2]);
                    e.setStartDateTime(p[3]);
                    e.setEndDateTime(p[4]);
                    e.setLocation(p[5]);
                    e.setCategory(p[6]);
                    list.add(e);
                }
            }
        }catch(FileNotFoundException e){
            System.err.println(e.getMessage());
        }
        return list;
    }

    public boolean updateEventInCsv(Event updatedEvent){
        List<Event> all=readAllEventsFromCsv();
        boolean found=false;
        for(int i=0;i<all.size();i++){
            if(all.get(i).getEventId()==updatedEvent.getEventId()){
                all.set(i,updatedEvent);
                found=true;
                break;
            }
        }
        if(found)rewriteAllEvents(all);
        return found;
    }

    public boolean deleteEventFromCsv(int eventId){
        List<Event> all=readAllEventsFromCsv();
        boolean removed=all.removeIf(e->e.getEventId()==eventId);
        if(removed)rewriteAllEvents(all);
        return removed;
    }

    private void rewriteAllEvents(List<Event> list){
        try(PrintWriter pw=new PrintWriter(new FileWriter(eventPath,false))){
            for(Event e:list){
                pw.println(e.getEventId()+"|"+e.getTitle()+"|"+e.getDescription()+"|"+e.getStartDateTime()+"|"+e.getEndDateTime()+"|"+e.getLocation()+"|"+e.getCategory());
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public synchronized void writeRecurrentEventToCsv(RecurrentEvent re){
        String line=re.getEventId()+"|"+re.getRecurrentInterval()+"|"+re.getRecurrentTimes()+"|"+re.getRecurrentEndDate();
        try(PrintWriter pw=new PrintWriter(new FileWriter(recurrentPath,true))){
            pw.println(line);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public List<RecurrentEvent> readAllRecurrentEventsFromCsv(){
        List<RecurrentEvent> list=new ArrayList<>();
        File f=new File(recurrentPath);
        if(!f.exists())return list;

        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine().trim();
                if(line.isEmpty())continue;
                String[] p=line.split("\\|");
                if(p.length>=4){
                    RecurrentEvent re=new RecurrentEvent();
                    re.setEventId(Integer.parseInt(p[0]));
                    re.setRecurrentInterval(p[1]);
                    re.setRecurrentTimes(Integer.parseInt(p[2]));
                    re.setRecurrentEndDate(p[3]);
                    list.add(re);
                }
            }
        }catch(FileNotFoundException e){
            System.err.println(e.getMessage());
        }
        return list;
    }

    public boolean updateRecurrentEventInCsv(RecurrentEvent updatedRe){
        List<RecurrentEvent> all=readAllRecurrentEventsFromCsv();
        boolean found=false;
        for(int i=0;i<all.size();i++){
            if(all.get(i).getEventId()==updatedRe.getEventId()){
                all.set(i,updatedRe);
                found=true;
                break;
            }
        }
        if(found)rewriteAllRecurrentEvents(all);
        return found;
    }

    public boolean deleteRecurrentEventFromCsv(int eventId){
        List<RecurrentEvent> all=readAllRecurrentEventsFromCsv();
        boolean removed=all.removeIf(re->re.getEventId()==eventId);
        if(removed)rewriteAllRecurrentEvents(all);
        return removed;
    }

    private void rewriteAllRecurrentEvents(List<RecurrentEvent> list){
        try(PrintWriter pw=new PrintWriter(new FileWriter(recurrentPath,false))){
            for(RecurrentEvent re:list){
                pw.println(re.getEventId()+"|"+re.getRecurrentInterval()+"|"+re.getRecurrentTimes()+"|"+re.getRecurrentEndDate());
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public synchronized void writeReminderConfigToFile(ReminderConfig rc){
        String line=rc.getEventId()+"|"+rc.getRemindDuration()+"|"+rc.isEnable();
        try(PrintWriter pw=new PrintWriter(new FileWriter(reminderPath,true))){
            pw.println(line);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public List<ReminderConfig> readAllReminderConfigs(){
        List<ReminderConfig> list=new ArrayList<>();
        File f=new File(reminderPath);
        if(!f.exists())return list;

        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine().trim();
                if(line.isEmpty())continue;
                String[] p=line.split("\\|");
                if(p.length>=3){
                    ReminderConfig rc=new ReminderConfig();
                    rc.setEventId(Integer.parseInt(p[0]));
                    rc.setRemindDuration(p[1]);
                    rc.setEnable(Boolean.parseBoolean(p[2]));
                    list.add(rc);
                }
            }
        }catch(FileNotFoundException e){
            System.err.println(e.getMessage());
        }
        return list;
    }

    public boolean backupAllData(String path){
        try(PrintWriter pw=new PrintWriter(new FileWriter(path,false))){
            pw.println("Event");
            for(Event e:readAllEventsFromCsv()){
                pw.println(e.getEventId()+"|"+e.getTitle()+"|"+e.getDescription()+"|"+e.getStartDateTime()+"|"+e.getEndDateTime()+"|"+e.getLocation()+"|"+e.getCategory());
            }
            pw.println("Recurrent");
            for(RecurrentEvent re:readAllRecurrentEventsFromCsv()){
                pw.println(re.getEventId()+"|"+re.getRecurrentInterval()+"|"+re.getRecurrentTimes()+"|"+re.getRecurrentEndDate());
            }
            pw.println("Reminder");
            for(ReminderConfig rc:readAllReminderConfigs()){
                pw.println(rc.getEventId()+"|"+rc.getRemindDuration()+"|"+rc.isEnable());
            }
            return true;
        }catch(IOException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean restoreData(String path,boolean isCoverExisting){
        File f=new File(path);
        if(!f.exists())return false;

        if(isCoverExisting){
            try{
                new PrintWriter(eventPath).close();
                new PrintWriter(recurrentPath).close();
                new PrintWriter(reminderPath).close();
            }catch(Exception e){}
        }

        try(Scanner s=new Scanner(f);PrintWriter evWriter=new PrintWriter(new FileWriter(eventPath,true));PrintWriter rcWriter=new PrintWriter(new FileWriter(recurrentPath,true));PrintWriter rmWriter=new PrintWriter(new FileWriter(reminderPath,true))){
            String section="";
            while(s.hasNextLine()){
                String line=s.nextLine().trim();
                if(line.isEmpty())continue;

                if(line.equalsIgnoreCase("Event")){section="EV";continue;}
                if(line.equalsIgnoreCase("Recurrent")){section="RC";continue;}
                if(line.equalsIgnoreCase("Reminder")){section="RM";continue;}

                if(section.equals("EV"))evWriter.println(line);
                else if(section.equals("RC"))rcWriter.println(line);
                else if(section.equals("RM"))rmWriter.println(line);
            }
            return true;
        }catch(IOException e){
            System.err.println(e.getMessage());
            return false;
        }
    }
}