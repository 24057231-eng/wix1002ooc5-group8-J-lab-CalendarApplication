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
    
    public void save_event(Event a){
        //Use object a to call its get method to obtain the attribute value
        String event=a.get_event_ID()+","+a.get_title()+","+a.get_description()+","+a.get_start_time()+","+a.get_end_time();
        
        //Write "line" into the file
        try(FileWriter fw=new FileWriter(path,true)){// true represents enabling append mode
            //Wrap it in a PrintWriter.
            PrintWriter pw=new PrintWriter(fw);
            pw.println(event);
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public int get_ID(){
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
                    String[] event=line.split(",");
                    if(Integer.parseInt(event[0])>count){
                        count=Integer.parseInt(event[0]);
                    }
                }
            }
        }catch(FileNotFoundException e){
            return 1;
        }
        
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
                    String[] event=line.split(",");
                    if(event.length>=5){
                        Event e=new Event();
                        e.set_event_ID(Integer.parseInt(event[0]));
                        e.set_title(event[1]);
                        e.set_description(event[2]);
                        e.set_start_time(event[3]);
                        e.set_end_time(event[4]);
                        al.add(e);
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
        
        // Return the fully-filled box
        return al;
    }
    
    public void save_recurrent(int ID, String interval, int times, String end_time){
        String event=ID+","+interval+","+times+","+end_time;
        
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
        try(FileWriter fw=new FileWriter("backup.dat",false);PrintWriter pw=new PrintWriter(fw);){
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
}
