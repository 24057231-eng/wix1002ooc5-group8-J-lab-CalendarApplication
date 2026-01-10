/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class MonthPanel extends JPanel {
    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public MonthPanel(YearMonth ym, CalendarAppGUI mainApp) {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // Month Title in English
        JLabel monthLabel = new JLabel("  " + MONTH_NAMES[ym.getMonthValue() - 1], JLabel.LEFT);
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(monthLabel, BorderLayout.NORTH);

        // Day Grid (7 columns for Sun-Sat)
        JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBackground(Color.BLACK);

        LocalDate firstDay = ym.atDay(1);
        int offset = firstDay.getDayOfWeek().getValue() % 7; 

        // Padding for empty days at the start of the month
        for (int i = 0; i < offset; i++) grid.add(new JLabel(""));

        // Creating buttons for each day
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            JButton dateBtn = new JButton(String.valueOf(day));
            
            dateBtn.setForeground(Color.WHITE);
            dateBtn.setBackground(Color.BLACK);
            dateBtn.setOpaque(true);
            dateBtn.setBorderPainted(false);
            dateBtn.setFocusPainted(false);
            dateBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));

            // Interaction: Select date on click
            dateBtn.addActionListener(e -> mainApp.onDateSelected(date, dateBtn));
            
            grid.add(dateBtn);
        }
        add(grid, BorderLayout.CENTER);
    }
}