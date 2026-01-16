/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calenderApplication.dataLayer;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author 星飞
 */
public class EventIdGenerator {
    private static int maxId = 0;
    private static boolean idLoaded = false;
    
    public static int generateNextEventId() {
        if (idLoaded) {
            maxId++;
            return maxId;
        }

        int count = 0;
        try {
            File f = new File("event.csv");
            if (!f.exists()) {
                maxId = 0;
                idLoaded = true;
                return 1;
            }

            try (Scanner s = new Scanner(f)) {
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    String[] eventParts = line.split("\\|");
                    if (eventParts.length > 0) {
                        try {
                            int currentId = Integer.parseInt(eventParts[0]);
                            if (currentId > count) {
                                count = currentId;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            return 1;
        }

        maxId = count;
        idLoaded = true;
        maxId++;
        return maxId;
    }
}
