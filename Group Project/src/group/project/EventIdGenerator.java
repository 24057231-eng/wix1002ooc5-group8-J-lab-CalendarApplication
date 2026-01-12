/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

/**
 *
 * @author 星飞
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class EventIdGenerator {
    //Cache the current maximum ID
    private static int maxId=0;
    private static boolean idLoaded=false;
    
    public static int generateNextEventId(){
        //// If the ID has already been loaded, simply return the next one stored in the memory.
        if(idLoaded){
            maxId++;
            return maxId;
        }

        // Only when it is the first run or the file has not been loaded before, will the scanning of the file be carried out.
        int count=0;
        try{
            File f=new File("event.csv");
            if(!f.exists()){
                // The default maximum ID is 0. If the file does not exist, the next ID will be 1.
                return 1;
            }

            //Use try-with-resources to automatically close the Scanner
            try(Scanner s=new Scanner(f)){
                while(s.hasNextLine()){
                    String line=s.nextLine();
                    String[] eventParts=line.split("\\|");
                    if(Integer.parseInt(eventParts[0])>count){
                        count=Integer.parseInt(eventParts[0]);
                    }
                }
            }
        }catch(FileNotFoundException e){
            return 1;
        }

        // Update cache
        maxId=count;
        idLoaded=true;

        //Return to the next available ID
        maxId++;
        return maxId;
    }
}
