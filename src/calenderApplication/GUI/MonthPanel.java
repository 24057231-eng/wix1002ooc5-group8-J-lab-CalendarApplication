/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;//Define the package

import calenderApplication.businessLogic.*;//Import business logic
import javax.swing.*;//Import Swing components
import java.awt.*;//Import AWT components
import java.time.LocalDate;//Import LocalDate
import java.time.YearMonth;//Import YearMonth
import java.util.List;//Import List interface

public class MonthPanel extends JPanel{//Define class inheriting JPanel
    private YearMonth yearMonth;//Declare year and month
    private EventManager manager;//Declare event manager
    private ReminderManager reminderManager;//Declare reminder manager
    private Frame owner;//Declare owner frame

    public MonthPanel(int year,int month,EventManager manager,ReminderManager reminderManager,Frame owner){//Constructor
        this.yearMonth=YearMonth.of(year,month);//Set year month
        this.manager=manager;//Assign event manager
        this.reminderManager=reminderManager;//Assign reminder manager
        this.owner=owner;//Assign owner frame
        setLayout(new BorderLayout());//Set layout manager
        setBackground(Color.BLACK);//Set background color

        //Month Title Style
        JLabel title=new JLabel(yearMonth.getMonth().toString(),JLabel.LEFT);//Create title label
        title.setForeground(new Color(255,59,48));//Set text color
        title.setFont(new Font("SansSerif",Font.BOLD,18));//Set font style
        title.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));//Set label padding
        add(title,BorderLayout.NORTH);//Add title to top panel
        JPanel dayGrid=new JPanel(new GridLayout(0,7));//Create grid panel
        dayGrid.setOpaque(false);//Make grid transparent

        //Fill in the blank spaces before the month
        int startOffset=yearMonth.atDay(1).getDayOfWeek().getValue()%7;//Calculate start offset
        for(int i=0;i<startOffset;i++)dayGrid.add(new JLabel(""));//Add empty labels

        //Fill in the date
        for(int day=1;day<=yearMonth.lengthOfMonth();day++){//Loop through days
            final int d=day;//Finalize day variable
            LocalDate date=yearMonth.atDay(d);//Get local date
            
            //Check whether there are any events for this date
            boolean hasEvents=checkHasEvents(date);//Check for events

            DayButton btn=new DayButton(String.valueOf(day),hasEvents);//Create day button
            
            //Highlight today
            if(LocalDate.now().equals(date)){//Check if today
                btn.setForeground(new Color(255,59,48));//Set text color
                btn.setFont(new Font("SansSerif",Font.BOLD,14));//Set font bold
            }//End if

            btn.addActionListener(e->{//Add click listener
                EventListDialog listDialog=new EventListDialog(owner,date,manager,reminderManager);//Create dialog
                listDialog.setVisible(true);//Show dialog
                //After the dialog box is closed, recheck the data status and refresh the UI
                refreshDayButton(btn,date);//Refresh button
            });//End listener
            dayGrid.add(btn);//Add button to grid
        }//End loop
        add(dayGrid,BorderLayout.CENTER);//Add grid to center
    }//End constructor

    /**
     * Auxiliary method: Check if there are any events on a specific date
     */
private boolean checkHasEvents(LocalDate date){//Method to check events
        if(manager==null)return false;//Return false if null
        List<calenderApplication.businessLogic.Event>events=manager.getEventsForDate(date);//Get event list
        return events!=null&&!events.isEmpty();//Return true if not empty
    }//End checkHasEvents

    /**
     * Auxiliary method: Refresh the status of individual buttons after the operation is completed.
     */
    private void refreshDayButton(DayButton btn,LocalDate date){//Method to refresh button
        boolean hasEvents=checkHasEvents(date);//Check events again
        btn.setHasEvents(hasEvents);//Update button status
        btn.repaint();//Repaint button
    }//End refreshDayButton

    /**
     * Internal class: Date button with event indicator dot
     */
private static class DayButton extends JButton{//Inner class for button
        private boolean hasEvents;//Flag for events

        public DayButton(String text,boolean hasEvents){//Constructor
            super(text);//Call parent constructor
            this.hasEvents=hasEvents;//Set event flag
            setForeground(Color.WHITE);//Set text color
            setContentAreaFilled(false);//Disable content fill
            setBorderPainted(false);//Disable border paint
            setFocusPainted(false);//Disable focus paint
            setCursor(new Cursor(Cursor.HAND_CURSOR));//Set cursor style
        }//End constructor

        public void setHasEvents(boolean hasEvents){//Setter for flag
            this.hasEvents=hasEvents;//Update flag
        }//End setter

        @Override//Override paint method
        protected void paintComponent(Graphics g){//Paint component
            if(getModel().isPressed()){//Check if pressed
                g.setColor(new Color(60,60,60));//Set press color
                g.fillOval(5,5,getWidth()-10,getHeight()-10);//Draw circle background
            }//End if
            
            super.paintComponent(g);//Call super paint
            if(hasEvents){//If has events
                Graphics2D g2d=(Graphics2D)g;//Cast to Graphics2D
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//Enable antialiasing
                g2d.setColor(new Color(150,150,150));//Set dot color
                g2d.fillOval((getWidth()-4)/2,getHeight()-10,4,4);//Draw small dot
            }//End if
        }//End paintComponent
    }//End inner class
}//End class