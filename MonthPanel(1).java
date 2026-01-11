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
    private YearMonth yearMonth;
    private EventManager manager;
    private Frame owner;

    public MonthPanel(int year, int month, EventManager manager, Frame owner) {
        this.yearMonth = YearMonth.of(year, month);
        this.manager = manager;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel title = new JLabel(yearMonth.getMonth().toString(), JLabel.LEFT);
        title.setForeground(new Color(255, 59, 48)); // Apple Red
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel dayGrid = new JPanel(new GridLayout(0, 7));
        dayGrid.setOpaque(false);

        // Fill empty days before start of month
        int startOffset = yearMonth.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < startOffset; i++) dayGrid.add(new JLabel(""));

        // Add day buttons
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            final int d = day;
            JButton btn = new JButton(String.valueOf(day));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);

            // Highlight Today
            if (LocalDate.now().equals(yearMonth.atDay(day))) {
                btn.setForeground(new Color(255, 59, 48));
                btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            }

            btn.addActionListener(e -> {
                new EventEditDialog(owner, manager, yearMonth.atDay(d)).setVisible(true);
            });
            dayGrid.add(btn);
        }
        add(dayGrid, BorderLayout.CENTER);
    }
}