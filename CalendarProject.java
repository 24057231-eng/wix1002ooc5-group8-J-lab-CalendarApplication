/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package calendarproject;


import javax.swing.SwingUtilities;

/**
 * Main Entry Point for the Calendar Application.
 */
public class CalendarProject {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalendarAppGUI gui = new CalendarAppGUI();
            gui.setVisible(true);
        });
    }
}