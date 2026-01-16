/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;//Define the package name

import calenderApplication.businessLogic.EventManager;//Import the EventManager class
import calenderApplication.businessLogic.ReminderManager;//Import the ReminderManager class
import javax.swing.*;//Import Swing GUI components
import java.awt.*;//Import AWT graphics components

public class CalendarAppGUI extends JFrame{//Define the GUI class inheriting from JFrame
    private EventManager manager;//Declare the event manager object
    private ReminderManager reminderManager;//Declare the reminder manager object

    public CalendarAppGUI(EventManager evManager,ReminderManager remManager){//Constructor to initialize the GUI
        this.manager=evManager;//Assign the event manager
        this.reminderManager=remManager;//Assign the reminder manager

        setTitle("2026 Smart Calendar");//Set the window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Stop program when window closes
        setSize(1100,850);//Set the window dimensions
        getContentPane().setBackground(Color.BLACK);//Set the background color to black

        JPanel yearGrid=new JPanel(new GridLayout(3,4,15,15));//Create a panel with a 3x4 grid layout
        yearGrid.setBackground(Color.BLACK);//Set panel background to black
        yearGrid.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));//Add padding around the panel

        for(int i=1;i<=12;i++){//Loop from 1 to 12 for each month
            yearGrid.add(new MonthPanel(2026,i,manager,reminderManager,this));//Add a month panel to the grid
        }

        JScrollPane scrollPane=new JScrollPane(yearGrid);//Add a scroll bar to the grid
        scrollPane.setBorder(null);//Remove the border from the scroll pane
        add(scrollPane);//Add the scroll pane to the window
        setLocationRelativeTo(null);//Center the window on the screen
    }//End of constructor
}//End of class