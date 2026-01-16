/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package calenderApplication.GUI;//Define the package name

import calenderApplication.dataLayer.FileIOManager;//Import the file manager class
import calenderApplication.businessLogic.*;//Import all business logic classes
import java.util.List;//Import List interface
import javax.swing.*;//Import Swing GUI components
import java.util.Timer;//Import Timer class
import java.util.TimerTask;//Import TimerTask class

public class AppInitializer{//Define the main class of the application
    public static void main(String[]args){//The main method where the program starts

        //Initialize the data layer
        FileIOManager ioManager=new FileIOManager();//Create the file manager object

        //Initialize the business layer and inject dependencies
        EventManager eventManager=new EventManager(ioManager);//Create the event manager
        ReminderManager reminderManager=new ReminderManager(eventManager,ioManager);//Create the reminder manager

        //Start the GUI and inject the business manager
        SwingUtilities.invokeLater(()->{//Run the GUI in the event dispatch thread
            CalendarAppGUI gui=new CalendarAppGUI(eventManager,reminderManager);//Create the main GUI window
            gui.setVisible(true);//Make the GUI window visible
        });//End of GUI thread block
        
        startReminderDaemon(reminderManager);//Start the background reminder system
    }//End of main method

    //Background guardian thread: Checks every 60 seconds for reminders
    private static void startReminderDaemon(ReminderManager reminderManager){//Method to start the background timer
        Timer timer=new Timer(true);//Create a new daemon timer
        timer.scheduleAtFixedRate(new TimerTask(){//Schedule a repeating task
            @Override//Override the run method
            public void run(){//The logic to execute periodically
                List<String>activeReminders=reminderManager.getUpcomingReminders();//Get a list of active reminders
                if(!activeReminders.isEmpty()){//Check if the list is not empty
                    for(String msg:activeReminders){//Loop through each reminder message
                        //In the UI thread, a reminder window is popped up
                        SwingUtilities.invokeLater(()->{//Update the UI safely
                            JOptionPane.showMessageDialog(null,msg,"Event Reminder",//Show a message dialog
                                JOptionPane.INFORMATION_MESSAGE);//Set the icon to information
                        });//End of UI update block
                    }//End of for loop
                }//End of if statement
            }//End of run method
        },5000,60000);//Start after 5 seconds and repeat every 60 seconds
    }//End of startReminderDaemon method
}//End of class
