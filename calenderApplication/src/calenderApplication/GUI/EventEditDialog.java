/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.GUI;

import calenderApplication.businessLogic.Event; 
import calenderApplication.businessLogic.EventManager;
import calenderApplication.businessLogic.ReminderManager;
import calenderApplication.businessLogic.RecurrentEvent;
import calenderApplication.businessLogic.ReminderConfig;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Vector;

public class EventEditDialog extends JDialog {

    private final EventManager eventManager;
    private final ReminderManager reminderManager;
    private final LocalDate selectedDate;

    private JTextField titleField = new JTextField();
    private JTextField locationField = new JTextField();
    private JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Work", "Personal", "Study", "Other"});
    
    private JComboBox<String> startHour = new JComboBox<>(generateNumberStrings(24));
    private JComboBox<String> startMin = new JComboBox<>(generateNumberStrings(60));
    private JComboBox<String> endHour = new JComboBox<>(generateNumberStrings(24));
    private JComboBox<String> endMin = new JComboBox<>(generateNumberStrings(60));

    private JCheckBox recurrentCheck = new JCheckBox("Enable Recurrence");
    private JComboBox<String> intervalBox = new JComboBox<>(new String[]{"1d", "1w", "2w", "4w"});
    private JTextField timesField = new JTextField("5");

    private JCheckBox reminderCheck = new JCheckBox("Enable Reminder");
    private JComboBox<String> durationBox = new JComboBox<>(new String[]{"PT15M", "PT30M", "PT1H", "P1D"});

    public EventEditDialog(Frame owner, LocalDate date, EventManager em, ReminderManager rm) {
        super(owner, "Create New Event", true);
        this.selectedDate = date;
        this.eventManager = em;
        this.reminderManager = rm;

        initComponents();
        setSize(450, 650);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(28, 28, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        styleComponent(titleField);
        styleComponent(locationField);
        styleComponent(categoryBox);
        styleComponent(startHour); styleComponent(startMin);
        styleComponent(endHour); styleComponent(endMin);
        styleComponent(intervalBox); styleComponent(timesField);
        styleComponent(durationBox);
        
        recurrentCheck.setForeground(Color.WHITE);
        recurrentCheck.setOpaque(false);
        reminderCheck.setForeground(Color.WHITE);
        reminderCheck.setOpaque(false);
        
        // Title
        addLabel(panel, "Title:", gbc, 0);
        gbc.gridx = 1; panel.add(titleField, gbc);

        // Location
        addLabel(panel, "Location:", gbc, 1);
        gbc.gridx = 1; panel.add(locationField, gbc);

        // Category
        addLabel(panel, "Category:", gbc, 2);
        gbc.gridx = 1; panel.add(categoryBox, gbc);

        // Start Time
        addLabel(panel, "Start Time:", gbc, 3);
        gbc.gridx = 1; panel.add(createTimePanel(startHour, startMin), gbc);

        // End Time
        addLabel(panel, "End Time:", gbc, 4);
        gbc.gridx = 1; panel.add(createTimePanel(endHour, endMin), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(new JSeparator(JSeparator.HORIZONTAL), gbc);

        // Recurrence Section
        gbc.gridy = 6; panel.add(recurrentCheck, gbc);
        addLabel(panel, "Interval & Times:", gbc, 7);
        JPanel recPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        recPanel.setOpaque(false);
        recPanel.add(intervalBox);
        recPanel.add(new JLabel(" x ")).setForeground(Color.WHITE);
        recPanel.add(timesField);
        gbc.gridx = 1; panel.add(recPanel, gbc);

        // Reminder Section
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panel.add(reminderCheck, gbc);
        addLabel(panel, "Remind Before:", gbc, 9);
        gbc.gridx = 1; panel.add(durationBox, gbc);

        // Save Button
        JButton saveBtn = new JButton("Save Event");
        saveBtn.setBackground(new Color(10, 132, 255));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.addActionListener(e -> handleSave()); 

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 8, 8, 8);
        panel.add(saveBtn, gbc);

        add(panel);
    }

    /**
     * handleSave: Core business processing logic
     */
    private void handleSave() {
        try {
            // Clearly use the Event class from the business layer to avoid conflicts with java.awt.Event.
            calenderApplication.businessLogic.Event event = new calenderApplication.businessLogic.Event();
            
            event.setTitle(titleField.getText());
            event.setLocation(locationField.getText());
            event.setCategory((String) categoryBox.getSelectedItem());
            event.setAttendees(new ArrayList<>()); 

            // Analyze the time data
            int sh = Integer.parseInt((String) startHour.getSelectedItem());
            int sm = Integer.parseInt((String) startMin.getSelectedItem());
            int eh = Integer.parseInt((String) endHour.getSelectedItem());
            int em = Integer.parseInt((String) endMin.getSelectedItem());
            
            event.setStartDateTime(selectedDate.atTime(sh, sm));
            event.setEndDateTime(selectedDate.atTime(eh, em));

            // Construct a repetitive logical object 
            RecurrentEvent recurrent = null;
            if (recurrentCheck.isSelected()) {
                recurrent = new RecurrentEvent();
                recurrent.setEnabled(true);
                recurrent.setRecurrentInterval((String) intervalBox.getSelectedItem());
                recurrent.setRecurrentTimes(Integer.parseInt(timesField.getText()));
                recurrent.setRecurrentEndDate("null"); 
            }

            if (eventManager.createEvent(event, recurrent)) {
                
                if (reminderCheck.isSelected()) {
                    String durationStr = (String) durationBox.getSelectedItem();
                    ReminderConfig config = new ReminderConfig(
                        event.getEventId(), 
                        Duration.parse(durationStr), 
                        true
                    );
                    reminderManager.setReminder(config);
                }
                
                JOptionPane.showMessageDialog(this, "Event saved successfully!");
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Conflict detected or invalid time range!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for recurrence times.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private JPanel createTimePanel(JComboBox<String> h, JComboBox<String> m) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        p.add(h);
        JLabel colon = new JLabel(":");
        colon.setForeground(Color.WHITE);
        p.add(colon);
        p.add(m);
        return p;
    }

    private Vector<String> generateNumberStrings(int limit) {
        Vector<String> v = new Vector<>();
        for (int i = 0; i < limit; i++) {
            v.add(String.format("%02d", i));
        }
        return v;
    }

    private void addLabel(JPanel p, String text, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        p.add(label, gbc);
    }

    private void styleComponent(JComponent c) {
        c.setBackground(new Color(44, 44, 46));
        c.setForeground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 62)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
}