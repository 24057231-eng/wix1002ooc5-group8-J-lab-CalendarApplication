/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;//Define the package

import calenderApplication.businessLogic.EventManager;//Import event manager
import calenderApplication.businessLogic.ReminderManager;//Import reminder manager
import javax.swing.*;//Import Swing components
import java.awt.*;//Import AWT components
import java.time.LocalDate;//Import LocalDate class
import java.util.List;//Import List interface

public class EventListDialog extends JDialog{//Define dialog class
    private final EventManager eventManager;//Declare event manager
    private final ReminderManager reminderManager;//Declare reminder manager
    private final LocalDate selectedDate;//Declare selected date
    private final Frame owner;//Declare owner frame
    
    //Use the packaging class or custom list items to store Event objects
    private DefaultListModel<calenderApplication.businessLogic.Event>listModel;//Declare list model
    private JList<calenderApplication.businessLogic.Event>eventJList;//Declare list component

    public EventListDialog(Frame owner,LocalDate date,EventManager em,ReminderManager rm){//Constructor
        super(owner,"Events on "+date,true);//Call parent constructor
        this.owner=owner;//Assign owner
        this.selectedDate=date;//Assign date
        this.eventManager=em;//Assign event manager
        this.reminderManager=rm;//Assign reminder manager

        setSize(450,550);//Set window size
        setLayout(new BorderLayout());//Set layout manager
        getContentPane().setBackground(new Color(30,30,30));//Set background color

        JPanel topPanel=new JPanel(new BorderLayout());//Create top panel
        topPanel.setOpaque(false);//Make transparent
        topPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));//Set borders
        
        JLabel dateLabel=new JLabel("Schedule: "+date);//Create date label
        dateLabel.setForeground(Color.WHITE);//Set text color
        dateLabel.setFont(new Font("SansSerif",Font.BOLD,16));//Set font
        
        JButton createBtn=new JButton("+ Create New");//Create button
        styleButton(createBtn,new Color(48,209,88));//Style button
        createBtn.addActionListener(e->{//Add click listener
            dispose();//Close current dialog
            new EventEditDialog(owner,date,eventManager,reminderManager).setVisible(true);//Open edit dialog
        });//End listener

        topPanel.add(dateLabel,BorderLayout.WEST);//Add label to panel
        topPanel.add(createBtn,BorderLayout.EAST);//Add button to panel
        add(topPanel,BorderLayout.NORTH);//Add top panel to dialog

        listModel=new DefaultListModel<>();//Initialize list model
        refreshListData();//Load data

        eventJList=new JList<>(listModel);//Create list
        eventJList.setBackground(new Color(44,44,46));//Set list background
        eventJList.setForeground(Color.WHITE);//Set list text color
        eventJList.setSelectionBackground(new Color(60,60,60));//Set selection color
        eventJList.setFont(new Font("SansSerif",Font.PLAIN,14));//Set font
        eventJList.setFixedCellHeight(40);//Set row height
        eventJList.setCellRenderer(new DefaultListCellRenderer(){//Set cell renderer
            @Override//Override method
            public Component getListCellRendererComponent(JList<?>list,Object value,int index,boolean isSelected,boolean cellHasFocus){//Render logic
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);//Call super
                if(value instanceof calenderApplication.businessLogic.Event){//Check type
                    calenderApplication.businessLogic.Event ev=(calenderApplication.businessLogic.Event)value;//Cast object
                    setText("  "+ev.getStartDateTimeAsLdt().toLocalTime()+" | "+ev.getTitle());//Set display text
                }//End if
                setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(60,60,60)));//Set separator line
                return this;//Return component
            }//End method
        });//End renderer

        JScrollPane scrollPane=new JScrollPane(eventJList);//Create scroll pane
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0,15,0,15));//Set border
        scrollPane.setOpaque(false);//Make transparent
        scrollPane.getViewport().setOpaque(false);//Make viewport transparent
        add(scrollPane,BorderLayout.CENTER);//Add scroll pane

        JPanel bottomPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));//Create bottom panel
        bottomPanel.setOpaque(false);//Make transparent
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10,15,15,15));//Set border

        JButton deleteBtn=new JButton("Delete Selected");//Create delete button
        styleButton(deleteBtn,new Color(255,69,58));//Style button
        deleteBtn.addActionListener(e->performDelete());//Add listener

        bottomPanel.add(deleteBtn);//Add button
        add(bottomPanel,BorderLayout.SOUTH);//Add bottom panel

        setLocationRelativeTo(owner);//Center window
    }//End constructor

    private void refreshListData(){//Method to refresh list
        listModel.clear();//Clear list
        List<calenderApplication.businessLogic.Event>events=eventManager.getEventsForDate(selectedDate);//Get events
        for(calenderApplication.businessLogic.Event ev:events){//Loop events
            listModel.addElement(ev);//Add to model
        }//End loop
    }//End method

    private void performDelete(){//Method to delete
        calenderApplication.businessLogic.Event selectedEvent=eventJList.getSelectedValue();//Get selection
        if(selectedEvent==null){//Check if null
            JOptionPane.showMessageDialog(this,"Please select an event to delete.");//Show warning
            return;//Exit method
        }//End if

        int confirm=JOptionPane.showConfirmDialog(this,//Show confirmation
            "Are you sure you want to delete: "+selectedEvent.getTitle()+"?",//Message
            "Confirm Delete",JOptionPane.YES_NO_OPTION);//Title and options

        if(confirm==JOptionPane.YES_OPTION){//If confirmed
            boolean success=eventManager.deleteEvent(selectedEvent.getEventId());//Delete event
            if(success){//If successful
                refreshListData();//Refresh list
                if(listModel.isEmpty()){//Check if empty
                    JOptionPane.showMessageDialog(this,"All events deleted for this day.");//Show message
                }//End if
            }else{//If failed
                JOptionPane.showMessageDialog(this,"Delete failed.");//Show error
            }//End else
        }//End if
    }//End method

    private void styleButton(JButton btn,Color bgColor){//Method to style button
        btn.setBackground(bgColor);//Set background
        btn.setForeground(Color.WHITE);//Set text color
        btn.setFocusPainted(false);//Remove focus paint
        btn.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));//Set padding
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));//Set cursor
    }//End method
}//End class