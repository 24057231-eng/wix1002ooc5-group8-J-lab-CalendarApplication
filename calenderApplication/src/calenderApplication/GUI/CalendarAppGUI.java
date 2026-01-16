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
import java.util.Locale;

public class CalendarAppGUI extends JFrame {
    private final EventManager manager;
    private final ReminderManager reminderManager;
    
    private CardLayout cardLayout;
    private JPanel mainContainer;
    
    private WeekPanel weekView;
    private JPanel yearView;
    private JPanel monthViewContainer;
    
    private String currentViewTag = "YEAR_VIEW";
    private LocalDate currentContextDate = LocalDate.now();

    public CalendarAppGUI(EventManager evManager, ReminderManager remManager) {
        this.manager = evManager;
        this.reminderManager = remManager;

        Locale.setDefault(Locale.ENGLISH);

        setTitle("2026 Smart Calendar - Professional Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 900);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        initNavigationBar();
        
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(Color.BLACK);

        initYearView();             
        initMonthViewContainer();   
        initWeekView(LocalDate.now()); 

        add(mainContainer, BorderLayout.CENTER);
        
        showView("YEAR_VIEW");
    }

    private void initNavigationBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        navBar.setBackground(new Color(20, 20, 20));
        
        JButton btnYear = new JButton("Year View");
        JButton btnMonth = new JButton("Month View");
        JButton btnWeek = new JButton("Week View");
        
        styleNavButton(btnYear);
        styleNavButton(btnMonth);
        styleNavButton(btnWeek);
        
        // 导航按钮切换逻辑
        btnYear.addActionListener(e -> showView("YEAR_VIEW"));
        btnMonth.addActionListener(e -> showMonthView(currentContextDate));
        btnWeek.addActionListener(e -> showWeekView(currentContextDate));
        
        navBar.add(btnYear);
        navBar.add(btnMonth);
        navBar.add(btnWeek);
        add(navBar, BorderLayout.NORTH);
    }

    private void initYearView() {
        yearView = new JPanel(new GridLayout(3, 4, 15, 15));
        yearView.setBackground(Color.BLACK);
        yearView.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 12; i++) {
            yearView.add(new MonthPanel(2026, i, manager, reminderManager, this));
        }

        JScrollPane scrollPane = new JScrollPane(yearView);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContainer.add(scrollPane, "YEAR_VIEW");
    }

    private void initMonthViewContainer() {
        monthViewContainer = new JPanel(new BorderLayout());
        monthViewContainer.setBackground(Color.BLACK);
        mainContainer.add(monthViewContainer, "MONTH_VIEW");
    }

    private void initWeekView(LocalDate date) {
        weekView = new WeekPanel(date, manager, reminderManager, this);
        mainContainer.add(weekView, "WEEK_VIEW");
    }

    public void refreshAllViews() {
        yearView.removeAll();
        for (int i = 1; i <= 12; i++) {
            yearView.add(new MonthPanel(2026, i, manager, reminderManager, this));
        }
        
        showMonthView(currentContextDate);
        
        weekView.refreshUI();
        
        yearView.revalidate();
        yearView.repaint();
    }

    public void showMonthView(LocalDate date) {
        this.currentContextDate = date;
        monthViewContainer.removeAll();
        MonthPanel mp = new MonthPanel(date.getYear(), date.getMonthValue(), manager, reminderManager, this);
        monthViewContainer.add(mp, BorderLayout.CENTER);
        
        showView("MONTH_VIEW");
        monthViewContainer.revalidate();
        monthViewContainer.repaint();
    }

    public void showWeekView(LocalDate date) {
        this.currentContextDate = date;
        mainContainer.remove(weekView);
        weekView = new WeekPanel(date, manager, reminderManager, this);
        mainContainer.add(weekView, "WEEK_VIEW");
        
        showView("WEEK_VIEW");
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void showView(String tag) {
        this.currentViewTag = tag;
        cardLayout.show(mainContainer, tag);
    }

    private void styleNavButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(44, 44, 46));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(44, 44, 46));
            }
        });
    }
}