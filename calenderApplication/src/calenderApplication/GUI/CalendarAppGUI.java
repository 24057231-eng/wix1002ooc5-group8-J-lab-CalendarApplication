/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI; 

import calenderApplication.businessLogic.EventManager;     
import calenderApplication.businessLogic.ReminderManager;   
import javax.swing.*;
import java.awt.*;

public class CalendarAppGUI extends JFrame {
    private EventManager manager;
    private ReminderManager reminderManager;

    public CalendarAppGUI(EventManager evManager, ReminderManager remManager) {
        this.manager = evManager;
        this.reminderManager = remManager;

        setTitle("2026 Smart Calendar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 850);
        getContentPane().setBackground(Color.BLACK);

        JPanel yearGrid = new JPanel(new GridLayout(3, 4, 15, 15));
        yearGrid.setBackground(Color.BLACK);
        yearGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 12; i++) {
            yearGrid.add(new MonthPanel(2026, i, manager, reminderManager, this));
        }

        JScrollPane scrollPane = new JScrollPane(yearGrid);
        scrollPane.setBorder(null);
        add(scrollPane);
        setLocationRelativeTo(null);
    }
}
