/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EventEditDialog extends JDialog {
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> eventList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    private String lastInput = null;

    public EventEditDialog(Frame owner, EventManager manager, LocalDate date) {
        super(owner, "Events for " + date, true);
        setLayout(new BorderLayout(10, 10));
        setSize(350, 450);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(new Color(28, 28, 30)); // Apple dark gray

        // List Setup
        eventList.setBackground(Color.BLACK);
        eventList.setForeground(Color.WHITE);
        eventList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        updateList(manager, date);

        // Input Setup
        inputField.setBackground(new Color(44, 44, 46));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addBtn = new JButton("Add Event");
        addBtn.setForeground(new Color(255, 59, 48)); // Apple Red

        addBtn.addActionListener(e -> {
            if (!inputField.getText().trim().isEmpty()) {
                lastInput = inputField.getText().trim();
                manager.addEvent(date, lastInput);
                inputField.setText("");
                updateList(manager, date);
            }
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int idx = eventList.getSelectedIndex();
            if (idx != -1) {
                manager.deleteEvent(date, idx);
                updateList(manager, date);
            }
        });

        // Layout
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.add(inputField);
        bottomPanel.add(addBtn);

        add(new JScrollPane(eventList), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.NORTH);
        add(deleteBtn, BorderLayout.SOUTH);
    }

    private void updateList(EventManager manager, LocalDate date) {
        listModel.clear();
        for (String s : manager.getEvents(date)) {
            listModel.addElement("• " + s);
        }
    }

    public String getInputText() { return lastInput; }
}