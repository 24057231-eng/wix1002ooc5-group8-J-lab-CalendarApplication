/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;

import calenderApplication.businessLogic.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class MonthPanel extends JPanel {
    private YearMonth yearMonth;
    private EventManager manager;
    private ReminderManager reminderManager;
    private Frame owner;

    public MonthPanel(int year, int month, EventManager manager, ReminderManager reminderManager, Frame owner) {
        this.yearMonth = YearMonth.of(year, month);
        this.manager = manager;
        this.reminderManager = reminderManager;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Month Title Style
        JLabel title = new JLabel(yearMonth.getMonth().toString(), JLabel.LEFT);
        title.setForeground(new Color(255, 59, 48));
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(title, BorderLayout.NORTH);
        JPanel dayGrid = new JPanel(new GridLayout(0, 7));
        dayGrid.setOpaque(false);

        // Fill in the blank spaces before the month.
        int startOffset = yearMonth.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < startOffset; i++) dayGrid.add(new JLabel(""));

        // Fill in the date
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            final int d = day;
            LocalDate date = yearMonth.atDay(d);
            
            //Check whether there are any events for this date (make sure that EventManager.getEventsForDate has been implemented)
            boolean hasEvents = checkHasEvents(date);

            DayButton btn = new DayButton(String.valueOf(day), hasEvents);
            
            //Highlight today
            if (LocalDate.now().equals(date)) {
                btn.setForeground(new Color(255, 59, 48));
                btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            }

            btn.addActionListener(e -> {
                EventListDialog listDialog = new EventListDialog(owner, date, manager, reminderManager);
                listDialog.setVisible(true);
                //  After the dialog box is closed, recheck the data status and refresh the UI
                refreshDayButton(btn, date);
            });
            dayGrid.add(btn);
        }
        add(dayGrid, BorderLayout.CENTER);
    }

    /**
     * Auxiliary method: Check if there are any events on a specific date
     */
private boolean checkHasEvents(LocalDate date) {
        if (manager == null) return false;
        List<calenderApplication.businessLogic.Event> events = manager.getEventsForDate(date);
        return events != null && !events.isEmpty();
    }

    /**
     * Auxiliary method: Refresh the status of individual buttons after the operation is completed.
     */
    private void refreshDayButton(DayButton btn, LocalDate date) {
        boolean hasEvents = checkHasEvents(date);
        btn.setHasEvents(hasEvents);
        btn.repaint(); 
    }

    /**
     * Internal class: Date button with event indicator dot
     */
private static class DayButton extends JButton {
        private boolean hasEvents;

        public DayButton(String text, boolean hasEvents) {
            super(text);
            this.hasEvents = hasEvents;
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setHasEvents(boolean hasEvents) {
            this.hasEvents = hasEvents;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) {
                g.setColor(new Color(60, 60, 60));
                g.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
            }
            
            super.paintComponent(g);
            if (hasEvents) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(150, 150, 150)); 
                g2d.fillOval((getWidth() - 4) / 2, getHeight() - 10, 4, 4);
            }
        }
    }
}