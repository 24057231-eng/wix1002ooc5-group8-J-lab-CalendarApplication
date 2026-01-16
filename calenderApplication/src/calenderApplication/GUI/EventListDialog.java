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
import java.util.List;

public class EventListDialog extends JDialog {
    private final EventManager eventManager;
    private final ReminderManager reminderManager;
    private final LocalDate selectedDate;
    private final Frame owner;
    
    // Use the packaging class or custom list items to store Event objects
    private DefaultListModel<calenderApplication.businessLogic.Event> listModel;
    private JList<calenderApplication.businessLogic.Event> eventJList;

    public EventListDialog(Frame owner, LocalDate date, EventManager em, ReminderManager rm) {
        super(owner, "Events on " + date, true);
        this.owner = owner;
        this.selectedDate = date;
        this.eventManager = em;
        this.reminderManager = rm;

        setSize(450, 550);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel dateLabel = new JLabel("Schedule: " + date);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JButton createBtn = new JButton("+ Create New");
        styleButton(createBtn, new Color(48, 209, 88));
        createBtn.addActionListener(e -> {
            dispose();
            new EventEditDialog(owner, date, eventManager, reminderManager).setVisible(true);
        });

        topPanel.add(dateLabel, BorderLayout.WEST);
        topPanel.add(createBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        refreshListData(); 

        eventJList = new JList<>(listModel);
        eventJList.setBackground(new Color(44, 44, 46));
        eventJList.setForeground(Color.WHITE);
        eventJList.setSelectionBackground(new Color(60, 60, 60));
        eventJList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eventJList.setFixedCellHeight(40);
        eventJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof calenderApplication.businessLogic.Event) {
                    calenderApplication.businessLogic.Event ev = (calenderApplication.businessLogic.Event) value;
                    setText("  " + ev.getStartDateTimeAsLdt().toLocalTime() + " | " + ev.getTitle());
                }
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(eventJList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn, new Color(255, 69, 58));
        deleteBtn.addActionListener(e -> performDelete());

        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(owner);
    }

    private void refreshListData() {
        listModel.clear();
        List<calenderApplication.businessLogic.Event> events = eventManager.getEventsForDate(selectedDate);
        for (calenderApplication.businessLogic.Event ev : events) {
            listModel.addElement(ev);
        }
    }

    private void performDelete() {
        calenderApplication.businessLogic.Event selectedEvent = eventJList.getSelectedValue();
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete: " + selectedEvent.getTitle() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = eventManager.deleteEvent(selectedEvent.getEventId());
            if (success) {
                refreshListData(); 
                if (listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All events deleted for this day.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}