/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;//Define the package

import calenderApplication.businessLogic.Event;//Import Event class
import calenderApplication.businessLogic.EventManager;//Import EventManager class
import calenderApplication.businessLogic.ReminderManager;//Import ReminderManager class
import calenderApplication.businessLogic.RecurrentEvent;//Import RecurrentEvent class
import calenderApplication.businessLogic.ReminderConfig;//Import ReminderConfig class

import javax.swing.*;//Import Swing components
import java.awt.*;//Import AWT components
import java.time.Duration;//Import Duration class
import java.time.LocalDate;//Import LocalDate class
import java.util.ArrayList;//Import ArrayList class
import java.util.Vector;//Import Vector class

public class EventEditDialog extends JDialog{//Define dialog class inheriting JDialog

    private final EventManager eventManager;//Declare event manager
    private final ReminderManager reminderManager;//Declare reminder manager
    private final LocalDate selectedDate;//Declare selected date

    private JTextField titleField=new JTextField();//Create title text field
    private JTextField locationField=new JTextField();//Create location text field
    private JComboBox<String>categoryBox=new JComboBox<>(new String[]{"Work","Personal","Study","Other"});//Create category dropdown
    
    private JComboBox<String>startHour=new JComboBox<>(generateNumberStrings(24));//Create start hour dropdown
    private JComboBox<String>startMin=new JComboBox<>(generateNumberStrings(60));//Create start minute dropdown
    private JComboBox<String>endHour=new JComboBox<>(generateNumberStrings(24));//Create end hour dropdown
    private JComboBox<String>endMin=new JComboBox<>(generateNumberStrings(60));//Create end minute dropdown

    private JCheckBox recurrentCheck=new JCheckBox("Enable Recurrence");//Create recurrence checkbox
    private JComboBox<String>intervalBox=new JComboBox<>(new String[]{"1d","1w","2w","4w"});//Create interval dropdown
    private JTextField timesField=new JTextField("5");//Create repetition count field

    private JCheckBox reminderCheck=new JCheckBox("Enable Reminder");//Create reminder checkbox
    private JComboBox<String>durationBox=new JComboBox<>(new String[]{"PT15M","PT30M","PT1H","P1D"});//Create duration dropdown

    public EventEditDialog(Frame owner,LocalDate date,EventManager em,ReminderManager rm){//Constructor
        super(owner,"Create New Event",true);//Call parent constructor
        this.selectedDate=date;//Assign date
        this.eventManager=em;//Assign event manager
        this.reminderManager=rm;//Assign reminder manager

        initComponents();//Initialize UI components
        setSize(450,650);//Set window size
        setLocationRelativeTo(owner);//Center window relative to owner
    }//End of constructor

    private void initComponents(){//Method to initialize components
        JPanel panel=new JPanel(new GridBagLayout());//Create panel with GridBagLayout
        panel.setBackground(new Color(28,28,30));//Set background color
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));//Set panel border
        GridBagConstraints gbc=new GridBagConstraints();//Create layout constraints
        gbc.insets=new Insets(8,8,8,8);//Set padding
        gbc.fill=GridBagConstraints.HORIZONTAL;//Set fill mode

        styleComponent(titleField);//Style title field
        styleComponent(locationField);//Style location field
        styleComponent(categoryBox);//Style category box
        styleComponent(startHour);styleComponent(startMin);//Style time fields
        styleComponent(endHour);styleComponent(endMin);//Style time fields
        styleComponent(intervalBox);styleComponent(timesField);//Style recurrence fields
        styleComponent(durationBox);//Style duration box
        
        recurrentCheck.setForeground(Color.WHITE);//Set text color
        recurrentCheck.setOpaque(false);//Make transparent
        reminderCheck.setForeground(Color.WHITE);//Set text color
        reminderCheck.setOpaque(false);//Make transparent
        
        //Title
        addLabel(panel,"Title:",gbc,0);//Add title label
        gbc.gridx=1;panel.add(titleField,gbc);//Add title field

        //Location
        addLabel(panel,"Location:",gbc,1);//Add location label
        gbc.gridx=1;panel.add(locationField,gbc);//Add location field

        //Category
        addLabel(panel,"Category:",gbc,2);//Add category label
        gbc.gridx=1;panel.add(categoryBox,gbc);//Add category box

        //Start Time
        addLabel(panel,"Start Time:",gbc,3);//Add start time label
        gbc.gridx=1;panel.add(createTimePanel(startHour,startMin),gbc);//Add start time panel

        //End Time
        addLabel(panel,"End Time:",gbc,4);//Add end time label
        gbc.gridx=1;panel.add(createTimePanel(endHour,endMin),gbc);//Add end time panel
        
        gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=2;//Set separator position
        panel.add(new JSeparator(JSeparator.HORIZONTAL),gbc);//Add horizontal separator

        //Recurrence Section
        gbc.gridy=6;panel.add(recurrentCheck,gbc);//Add recurrence checkbox
        addLabel(panel,"Interval & Times:",gbc,7);//Add interval label
        JPanel recPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));//Create recurrence sub-panel
        recPanel.setOpaque(false);//Make transparent
        recPanel.add(intervalBox);//Add interval box
        recPanel.add(new JLabel(" x ")).setForeground(Color.WHITE);//Add 'x' label
        recPanel.add(timesField);//Add times field
        gbc.gridx=1;panel.add(recPanel,gbc);//Add recurrence panel

        //Reminder Section
        gbc.gridx=0;gbc.gridy=8;gbc.gridwidth=2;//Set reminder position
        panel.add(reminderCheck,gbc);//Add reminder checkbox
        addLabel(panel,"Remind Before:",gbc,9);//Add duration label
        gbc.gridx=1;panel.add(durationBox,gbc);//Add duration box

        //Save Button
        JButton saveBtn=new JButton("Save Event");//Create save button
        saveBtn.setBackground(new Color(10,132,255));//Set button background
        saveBtn.setForeground(Color.WHITE);//Set button text color
        saveBtn.setFont(new Font("SansSerif",Font.BOLD,14));//Set button font
        saveBtn.addActionListener(e->handleSave());//Add click listener

        gbc.gridx=0;gbc.gridy=10;gbc.gridwidth=2;//Set button position
        gbc.insets=new Insets(25,8,8,8);//Set button padding
        panel.add(saveBtn,gbc);//Add button to panel

        add(panel);//Add main panel to dialog
    }//End of initComponents

    /**
     * handleSave: Core business processing logic
     */
    private void handleSave(){//Method to save event
        try{//Start try block
            //Use specific Event class to avoid conflicts
            calenderApplication.businessLogic.Event event=new calenderApplication.businessLogic.Event();//Create new event object
            
            event.setTitle(titleField.getText());//Set event title
            event.setLocation(locationField.getText());//Set event location
            event.setCategory((String)categoryBox.getSelectedItem());//Set event category
            event.setAttendees(new ArrayList<>());//Initialize attendees list

            //Analyze the time data
            int sh=Integer.parseInt((String)startHour.getSelectedItem());//Parse start hour
            int sm=Integer.parseInt((String)startMin.getSelectedItem());//Parse start minute
            int eh=Integer.parseInt((String)endHour.getSelectedItem());//Parse end hour
            int em=Integer.parseInt((String)endMin.getSelectedItem());//Parse end minute
            
            event.setStartDateTime(selectedDate.atTime(sh,sm));//Set start date time
            event.setEndDateTime(selectedDate.atTime(eh,em));//Set end date time

            //Construct a repetitive logical object
            RecurrentEvent recurrent=null;//Initialize recurrence object
            if(recurrentCheck.isSelected()){//Check if recurrence is enabled
                recurrent=new RecurrentEvent();//Create recurrence object
                recurrent.setEnabled(true);//Enable recurrence
                recurrent.setRecurrentInterval((String)intervalBox.getSelectedItem());//Set interval
                recurrent.setRecurrentTimes(Integer.parseInt(timesField.getText()));//Set repetition times
                recurrent.setRecurrentEndDate("null");//Set end date to null
            }//End of recurrence check

            if(eventManager.createEvent(event,recurrent)){//Try to create event
                
                if(reminderCheck.isSelected()){//Check if reminder is enabled
                    String durationStr=(String)durationBox.getSelectedItem();//Get duration string
                    ReminderConfig config=new ReminderConfig(//Create reminder config
                        event.getEventId(),//Set event ID
                        Duration.parse(durationStr),//Set duration
                        true//Set active status
                    );//End of config creation
                    reminderManager.setReminder(config);//Save reminder
                }//End of reminder check
                
                JOptionPane.showMessageDialog(this,"Event saved successfully!");//Show success message
                dispose();//Close dialog
            }else{//If creation failed
                JOptionPane.showMessageDialog(this,"Conflict detected or invalid time range!","Error",JOptionPane.ERROR_MESSAGE);//Show error
            }//End of create check
        }catch(NumberFormatException ex){//Catch number format error
            JOptionPane.showMessageDialog(this,"Please enter a valid number for recurrence times.");//Show warning
        }catch(Exception ex){//Catch generic error
            JOptionPane.showMessageDialog(this,"Save failed: "+ex.getMessage());//Show failure message
        }//End of try-catch
    }//End of handleSave

    private JPanel createTimePanel(JComboBox<String>h,JComboBox<String>m){//Method to create time selector
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));//Create panel with flow layout
        p.setOpaque(false);//Make transparent
        p.add(h);//Add hour box
        JLabel colon=new JLabel(":");//Create colon label
        colon.setForeground(Color.WHITE);//Set colon color
        p.add(colon);//Add colon label
        p.add(m);//Add minute box
        return p;//Return panel
    }//End of createTimePanel

    private Vector<String>generateNumberStrings(int limit){//Method to generate number strings
        Vector<String>v=new Vector<>();//Create vector
        for(int i=0;i<limit;i++){//Loop to limit
            v.add(String.format("%02d",i));//Add formatted number
        }//End of loop
        return v;//Return vector
    }//End of generateNumberStrings

    private void addLabel(JPanel p,String text,GridBagConstraints gbc,int y){//Method to add label
        gbc.gridx=0;gbc.gridy=y;gbc.gridwidth=1;//Set layout constraints
        JLabel label=new JLabel(text);//Create label
        label.setForeground(Color.LIGHT_GRAY);//Set label color
        p.add(label,gbc);//Add label to panel
    }//End of addLabel

    private void styleComponent(JComponent c){//Method to style components
        c.setBackground(new Color(44,44,46));//Set background color
        c.setForeground(Color.WHITE);//Set text color
        c.setBorder(BorderFactory.createCompoundBorder(//Set compound border
            BorderFactory.createLineBorder(new Color(60,60,62)),//Set line border
            BorderFactory.createEmptyBorder(5,5,5,5)//Set padding
        ));//End of border creation
    }//End of styleComponent
}//End of class