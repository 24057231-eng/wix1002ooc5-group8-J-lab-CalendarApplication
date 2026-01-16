/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package calenderApplication.GUI; 

import calenderApplication.dataLayer.FileIOManager; 
import calenderApplication.businessLogic.*;       
import java.util.List;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class AppInitializer {
    public static void main(String[] args) {

        // 1. Initialize the data layer
        FileIOManager ioManager = new FileIOManager();

        // 2. Initialize the business layer and inject dependencies
        EventManager eventManager = new EventManager(ioManager);
        ReminderManager reminderManager = new ReminderManager(eventManager, ioManager);

        // 3. Start the GUI and inject the business manager
        SwingUtilities.invokeLater(() -> {
            CalendarAppGUI gui = new CalendarAppGUI(eventManager, reminderManager);
            gui.setVisible(true);
        });
        
        startReminderDaemon(reminderManager);
    }

    /**
     * Background guardian thread: Checks every 60 seconds to see if any upcoming reminders are available.
     */
    private static void startReminderDaemon(ReminderManager reminderManager) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<String> activeReminders = reminderManager.getUpcomingReminders();
                if (!activeReminders.isEmpty()) {
                    for (String msg : activeReminders) {
                        // In the UI thread, a reminder window is popped up.
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, msg, "Event Reminder", 
                                JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                }
            }
        }, 5000, 60000); // It starts 5 seconds after startup and executes once every minute.
    }
}
