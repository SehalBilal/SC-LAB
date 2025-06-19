package view;

import javax.swing.*;
import java.awt.*;
import controller.OfferCoursesController;
import model.CourseModel;
import java.util.LinkedHashMap;
import java.util.List;

public class OfferCoursesView extends JFrame {
    public OfferCoursesView(OfferCoursesController controller, LinkedHashMap<String, List<CourseModel.CourseInfo>> offeredCourses) {
        setTitle("Offer Courses");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.DARK_GRAY);

        // Color palette for semesters (repeat if more semesters)
        Color[] semesterColors = {
            new Color(180, 255, 180), // light green
            new Color(200, 255, 255), // light blue
            new Color(255, 230, 180), // light orange
            new Color(255, 255, 180), // light yellow
            new Color(255, 200, 200), // light pink
            new Color(220, 200, 255), // light purple
            new Color(200, 255, 200), // light green 2
            new Color(200, 220, 255), // light blue 2
            new Color(255, 255, 255)  // white
        };
        int colorIdx = 0;

        for (String semester : offeredCourses.keySet()) {
            JPanel semPanel = new JPanel(new BorderLayout());
            semPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            semPanel.setBackground(semesterColors[colorIdx % semesterColors.length]);
            colorIdx++;

            JLabel semLabel = new JLabel(semester);
            semLabel.setFont(new Font("Arial", Font.BOLD, 20));
            semLabel.setForeground(Color.BLACK);
            semPanel.add(semLabel, BorderLayout.NORTH);

            // Table for courses
            String[] colNames = {"Course Code", "Course Title", "Faculty Members"};
            List<CourseModel.CourseInfo> courses = offeredCourses.get(semester);
            String[][] data = new String[courses.size()][3];
            for (int i = 0; i < courses.size(); i++) {
                CourseModel.CourseInfo c = courses.get(i);
                data[i][0] = c.code;
                data[i][1] = c.title;
                data[i][2] = c.faculty;
            }
            JTable table = new JTable(data, colNames);
            table.setFont(new Font("Arial", Font.PLAIN, 16));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
            table.setRowHeight(28);
            table.setEnabled(false);
            JScrollPane tableScroll = new JScrollPane(table);
            semPanel.add(tableScroll, BorderLayout.CENTER);

            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(semPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.addActionListener(e -> controller.backToDashboard());
        add(backBtn, BorderLayout.SOUTH);
    }
} 