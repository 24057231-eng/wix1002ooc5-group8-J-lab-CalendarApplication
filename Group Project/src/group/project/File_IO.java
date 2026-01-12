/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
public class File_IO {
    //Define the file path
    private static String path="event.csv";
    private static String rc_path="recurrent.csv";
    private static String r_path="reminder.csv";
    private static String b_path="backup.csv";
    
    //Cache the current maximum ID
    private static int max_ID=0;
    private static boolean ID_loaded=false;
    
    //Add "synchronized" to prevent conflicts in multi-threading.
    public synchronized void save_event(Event a){
        //Use object a to call its get method to obtain the attribute value
        String event=a.get_event_ID()+"|"+a.get_title()+"|"+a.get_description()+"|"+a.get_start_time()+"|"+a.get_end_time()+"|"+a.get_location()+"|"+a.get_category();
        
        //Write "line" into the file
        try(FileWriter fw=new FileWriter(path,true)){// true represents enabling append mode
            //Wrap it in a PrintWriter.
            PrintWriter pw=new PrintWriter(fw);
            pw.println(event);
            
            //After the saving is successful, update the ID in the memory to ensure that the next acquisition is a new one.
            if(a.get_event_ID()>max_ID){
                max_ID=a.get_event_ID();
                ID_loaded=true;
            }
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public int get_ID(){
        //// If the ID has already been loaded, simply return the next one stored in the memory.
        if(ID_loaded){
            return max_ID+1;
        }
        
        // Only when it is the first run or the file has not been loaded before, will the scanning of the file be carried out.
        int count=0;
        try{
            File f=new File(path);
            if(!f.exists()){
                // The default maximum ID is 0. If the file does not exist, the next ID will be 1.
                return 1;
            }
            
            //Use try-with-resources to automatically close the Scanner
            try(Scanner s=new Scanner(f)){
                while(s.hasNextLine()){
                    String line=s.nextLine();
                    String[] event=line.split("\\|");
                    if(Integer.parseInt(event[0])>count){
                        count=Integer.parseInt(event[0]);
                    }
                }
            }
        }catch(FileNotFoundException e){
            return 1;
        }
        
        // Update cache
        max_ID=count;
        ID_loaded=true;
        
        //Return to the next available ID
        return count+1;
    }
    
    public ArrayList<Event> read_event(){
        ArrayList<Event> al=new ArrayList<>();//Create an empty box
        File f=new File(path);
        
        // If the file does not exist, directly return an empty box to prevent errors.
        if(!f.exists()){
            return al;
        }
        
        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine();
                if(line.trim().isEmpty()){
                    continue;
                }else{
                    String[] event=line.split("\\|");
                    if(event.length>=7){
                        Event e=new Event();
                        e.set_event_ID(Integer.parseInt(event[0]));
                        e.set_title(event[1]);
                        e.set_description(event[2]);
                        e.set_start_time(event[3]);
                        e.set_end_time(event[4]);
                        e.set_location(event[5]);
                        e.set_category(event[6]);
                        al.add(e);
                        
                        // Update ID cache
                        if(e.get_event_ID()>max_ID){
                            max_ID=e.get_event_ID();
                            ID_loaded=true;
                        }
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
        
        // Return the fully-filled box
        return al;
    }
    
    public synchronized void save_recurrent(int ID, String interval, int times, String end_time){
        String event=ID+"|"+interval+"|"+times+"|"+end_time;
        
        //Write "line" into the file
        try(FileWriter fw=new FileWriter(rc_path,true)){// true represents enabling append mode
            //Wrap it in a PrintWriter.
            PrintWriter pw=new PrintWriter(fw);
            pw.println(event);
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public void backup_data(){
        try(FileWriter fw=new FileWriter(b_path,false);PrintWriter pw=new PrintWriter(fw);){
            File e=new File(path);
            if(e.exists()){
                Scanner ev=new Scanner(e);
                pw.println("Event");
                while(ev.hasNextLine()){
                    pw.println(ev.nextLine());
                }
                ev.close();
            }
            File r=new File(rc_path);
            if(r.exists()){
                Scanner re=new Scanner(r);
                pw.println();
                pw.println("Recurrent");
                while(re.hasNextLine()){
                    pw.println(re.nextLine());
                }
                re.close();
            }
        }catch(IOException i){
            System.out.println("Problem with file output.");
        }
    }
    
    public void rewrite_event(ArrayList<Event> allEvents){
        try(FileWriter fw=new FileWriter(path,false);PrintWriter pw=new PrintWriter(fw)){
            for(Event e: allEvents){
                String event=e.get_event_ID()+"|"+e.get_title()+"|"+e.get_description()+"|"+e.get_start_time()+"|"+e.get_end_time()+"|"+e.get_location()+"|"+e.get_category();
                pw.println(event);
            }
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public synchronized void save_reminder(Reminder a){
        String event=a.get_event_ID()+"|"+a.get_time()+"|"+a.get_message();
        try(FileWriter fw=new FileWriter(r_path,true);PrintWriter pw=new PrintWriter(fw)){
            pw.println(event);
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public ArrayList<Reminder> read_reminder(){
        ArrayList<Reminder> al=new ArrayList<>();
        File f=new File(r_path);
        if(!f.exists()){
            return al;
        }
        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine();
                if(line.trim().isEmpty()){
                    continue;
                }else{
                    String[] reminder=line.split("\\|");
                    if(reminder.length>=3){
                        Reminder r=new Reminder();
                        r.set_event_ID(Integer.parseInt(reminder[0]));
                        r.set_time(reminder[1]);
                        r.set_message(reminder[2]);
                        al.add(r);
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
        return al;
    }
    
    public void recover_data(){
        File f=new File(b_path);
        if(!f.exists()){
            return;
        }
        try(Scanner s=new Scanner(f)){
            try(FileWriter e=new FileWriter(path,false);PrintWriter ev=new PrintWriter(e)){
                try(FileWriter r=new FileWriter(rc_path,false);PrintWriter re=new PrintWriter(r)){
                    boolean check=false;
                    while(s.hasNextLine()){
                        String line=s.nextLine();
                        if(line.trim().isEmpty()){
                            continue;
                        }else if(line.equals("Event")){
                            check=false;
                        }else if(line.equals("Recurrent")){
                            check=true;
                        }else{
                            if(check){
                                re.println(line);
                            }else{
                                ev.println(line);
                            }
                        }
                    }
                }catch(IOException a){
                    System.out.println("Problem with file output.");
                }
            }catch(IOException b){
                System.out.println("Problem with file output.");
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
    }
}