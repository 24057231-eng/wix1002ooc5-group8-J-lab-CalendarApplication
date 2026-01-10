/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendarproject;


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarAppGUI extends JFrame {
    private EventManager manager = new EventManager();
    private LocalDate selectedDate = null;
    private JButton lastSelectedBtn = null;
    
    private JLabel detailTitle;
    private DefaultListModel<String> listModel;

    public CalendarAppGUI() {
        setTitle("WIX1002 Calendar Project 2026");
        setSize(450, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // --- Top Bar (iOS Style) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.BLACK);
        JLabel yearLbl = new JLabel(" 2026", JLabel.LEFT);
        yearLbl.setForeground(Color.RED);
        yearLbl.setFont(new Font("SansSerif", Font.BOLD, 32));
        
        JButton addBtn = new JButton("+");
        addBtn.setForeground(Color.RED);
        addBtn.setFont(new Font("Arial", Font.BOLD, 28));
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.addActionListener(e -> triggerAddEvent());
        topBar.add(yearLbl, BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);

        // --- Scrollable Calendar Area ---
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(Color.BLACK);
        for (int i = 1; i <= 12; i++) {
            scrollContent.add(new MonthPanel(YearMonth.of(2026, i), this));
            scrollContent.add(Box.createVerticalStrut(25));
        }
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        // --- Bottom Details Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(450, 220));
        bottomPanel.setBackground(new Color(20, 20, 20));
        
        detailTitle = new JLabel("Click a date to view events", JLabel.CENTER);
        detailTitle.setForeground(Color.LIGHT_GRAY);
        detailTitle.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        listModel = new DefaultListModel<>();
        JList<String> displayList = new JList<>(listModel);
        displayList.setBackground(Color.BLACK);
        displayList.setForeground(Color.WHITE);
        displayList.setFont(new Font("SansSerif", Font.PLAIN, 16));

        bottomPanel.add(detailTitle, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(displayList), BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void onDateSelected(LocalDate date, JButton btn) {
        // UI Feedback: Highlight selected date with Red
        if (lastSelectedBtn != null) lastSelectedBtn.setBackground(Color.BLACK);
        btn.setBackground(Color.RED);
        lastSelectedBtn = btn;

        this.selectedDate = date;
        detailTitle.setText("Events for: " + date);
        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        for (String s : manager.getEvents(selectedDate)) {
            listModel.addElement(" • " + s);
        }
    }

    private void triggerAddEvent() {
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date on the calendar first!");
            return;
        }
        EventEditDialog dialog = new EventEditDialog(this, selectedDate);
        dialog.setVisible(true);
        String task = dialog.getInputText();
        if (task != null) {
            manager.addEvent(selectedDate, task);
            refreshList();
        }
    }
}