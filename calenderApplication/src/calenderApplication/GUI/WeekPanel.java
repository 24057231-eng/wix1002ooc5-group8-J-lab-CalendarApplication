/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;

import calenderApplication.businessLogic.EventManager;
import calenderApplication.businessLogic.ReminderManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeekPanel extends JPanel {
    private final EventManager manager;
    private final ReminderManager reminderManager;
    private final Frame owner;
    private LocalDate weekStartDate; 

    public WeekPanel(LocalDate referenceDate, EventManager manager, ReminderManager reminderManager, Frame owner) {
        this.manager = manager;
        this.reminderManager = reminderManager;
        this.owner = owner;

        this.weekStartDate = referenceDate.minusDays(referenceDate.getDayOfWeek().getValue() - 1);
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        refreshUI();
    }

    public void refreshUI() {
        removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        LocalDate weekEndDate = weekStartDate.plusDays(6);
        JLabel titleLabel = new JLabel(weekStartDate.format(DateTimeFormatter.ofPattern("MMM dd")) + 
                                     " - " + weekEndDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        titleLabel.setForeground(new Color(255, 59, 48));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);

        JButton btnPrev = new JButton("< Last Week");
        JButton btnNext = new JButton("Next Week >");

        styleNavButton(btnPrev);
        styleNavButton(btnNext);

        btnPrev.addActionListener(e -> prevWeek());
        btnNext.addActionListener(e -> nextWeek());

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        topPanel.add(navPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 7, 5, 0));
        grid.setOpaque(false);

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            grid.add(createDayColumn(currentDate));
        }
        
        add(grid, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createDayColumn(LocalDate date) {
        JPanel column = new JPanel(new BorderLayout());
        column.setOpaque(true);
        column.setBackground(new Color(28, 28, 30));
        column.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 46)));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel dayName = new JLabel(date.getDayOfWeek().name().substring(0, 3));
        dayName.setForeground(Color.GRAY);
        dayName.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel dayNum = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayNum.setForeground(Color.WHITE);
        dayNum.setFont(new Font("SansSerif", Font.BOLD, 18));
        dayNum.setHorizontalAlignment(SwingConstants.CENTER);

        if (date.equals(LocalDate.now())) {
            dayNum.setForeground(new Color(255, 59, 48));
        }

        header.add(dayName);
        header.add(dayNum);
        column.add(header, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<calenderApplication.businessLogic.Event> events = manager.getEventsForDate(date);
        
        if (events != null) {
            for (calenderApplication.businessLogic.Event ev : events) {
                listModel.addElement(ev.getStartDateTimeAsLdt().toLocalTime() + " " + ev.getTitle());
            }
        }

        JList<String> eventList = new JList<>(listModel);
        eventList.setBackground(new Color(38, 38, 40));
        eventList.setForeground(new Color(200, 200, 200));
        eventList.setFont(new Font("SansSerif", Font.PLAIN, 11));

        eventList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EventListDialog listDialog = new EventListDialog(owner, date, manager, reminderManager);
                listDialog.setVisible(true);
                refreshUI();
            }
        });

        JScrollPane sp = new JScrollPane(eventList);
        sp.setBorder(null);
        column.add(sp, BorderLayout.CENTER);

        return column;
    }

    private void styleNavButton(JButton btn) {
        btn.setBackground(new Color(44, 44, 46));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 62)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void nextWeek() {
        weekStartDate = weekStartDate.plusWeeks(1);
        refreshUI();
    }

    public void prevWeek() {
        weekStartDate = weekStartDate.minusWeeks(1);
        refreshUI();
    }
}