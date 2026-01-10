/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EventEditDialog extends JDialog {
    private JTextField inputField;
    private boolean isSaved = false;

    public EventEditDialog(JFrame parent, LocalDate date) {
        super(parent, "Add Event: " + date, true);
        setSize(350, 150);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        inputField = new JTextField();
        JButton saveBtn = new JButton("Save Event");

        saveBtn.addActionListener(e -> {
            if (!inputField.getText().trim().isEmpty()) {
                isSaved = true;
                dispose();
            }
        });

        add(new JLabel("  Enter Event Description:"), BorderLayout.NORTH);
        add(inputField, BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);
    }

    public String getInputText() { 
        return isSaved ? inputField.getText() : null; 
    }
}