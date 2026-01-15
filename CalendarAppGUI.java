/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;

import javax.swing.*;
import java.awt.*;

public class CalendarAppGUI extends JFrame {
    private EventManager manager = new EventManager();

    public CalendarAppGUI() {
        setTitle("2026 Calendar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        getContentPane().setBackground(Color.BLACK);

        JPanel yearGrid = new JPanel(new GridLayout(3, 4, 20, 20));
        yearGrid.setBackground(Color.BLACK);
        yearGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add 12 months
        for (int i = 1; i <= 12; i++) {
            yearGrid.add(new MonthPanel(2026, i, manager, this));
        }

        add(new JScrollPane(yearGrid));
        setLocationRelativeTo(null);
    }
}
