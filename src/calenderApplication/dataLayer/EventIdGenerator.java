/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.dataLayer;//Define the package

import java.util.Scanner;//Import Scanner class
import java.io.File;//Import File class
import java.io.FileNotFoundException;//Import exception class

/**
 *
 */
public class EventIdGenerator{//Define the class
    private static int maxId=0;//Store the maximum ID found
    private static boolean idLoaded=false;//Flag to check if ID is loaded
    
    public static int generateNextEventId(){//Method to generate next ID
        if(idLoaded){//Check if ID is already loaded
            maxId++;//Increment the ID
            return maxId;//Return the new ID
        }//End if

        int count=0;//Initialize count variable
        try{//Start try block
            File f=new File("event.csv");//Create file object
            if(!f.exists()){//Check if file exists
                maxId=0;//Set max ID to 0
                idLoaded=true;//Set loaded flag to true
                return 1;//Return first ID
            }//End if

            try(Scanner s=new Scanner(f)){//Try to read file with Scanner
                while(s.hasNextLine()){//Loop through lines
                    String line=s.nextLine();//Read current line
                    String[]eventParts=line.split("\\|");//Split line by pipe symbol
                    if(eventParts.length>0){//Check if parts exist
                        try{//Try to parse integer
                            int currentId=Integer.parseInt(eventParts[0]);//Parse the ID part
                            if(currentId>count){//Check if current ID is larger
                                count=currentId;//Update the count
                            }//End if
                        }catch(NumberFormatException e){//Catch parsing errors
                        }//End catch
                    }//End if
                }//End while
            }//End try resource
        }catch(FileNotFoundException e){//Catch file not found error
            return 1;//Return default ID
        }//End catch

        maxId=count;//Set maxId to found count
        idLoaded=true;//Set loaded flag to true
        maxId++;//Increment maxId
        return maxId;//Return the next ID
    }//End method
}//End class