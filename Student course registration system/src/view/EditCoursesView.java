package view;

import javax.swing.*;
import java.awt.*;
import controller.EditCoursesController;
import model.CourseModel;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.border.TitledBorder;

public class EditCoursesView extends JFrame {
    private EditCoursesController controller;
    private LinkedHashMap<String, JList<CourseModel.CourseInfo>> semesterLists = new LinkedHashMap<>();
    private JPanel mainPanel;

    public EditCoursesView(EditCoursesController controller, LinkedHashMap<String, List<CourseModel.CourseInfo>> offeredCourses) {
        this.controller = controller;
        setTitle("Edit Courses");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));

        for (String semester : offeredCourses.keySet()) {
            JPanel semPanel = new JPanel(new BorderLayout());
            semPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,160,120), 2), semester, TitledBorder.LEFT, TitledBorder.TOP, new Font("Century Gothic", Font.BOLD, 18), new Color(0,160,120)));
            semPanel.setBackground(Color.WHITE);
            DefaultListModel<CourseModel.CourseInfo> listModel = new DefaultListModel<>();
            for (CourseModel.CourseInfo c : offeredCourses.get(semester)) listModel.addElement(c);
            JList<CourseModel.CourseInfo> courseList = new JList<>(listModel);
            courseList.setCellRenderer(new CourseCellRenderer());
            courseList.setFont(new Font("Arial", Font.PLAIN, 16));
            courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            semesterLists.put(semester, courseList);
            JScrollPane listScroll = new JScrollPane(courseList);
            listScroll.setPreferredSize(new Dimension(800, 120));
            semPanel.add(listScroll, BorderLayout.CENTER);

            // Context menu
            JPopupMenu semMenu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem("Add Course");
            addItem.addActionListener(e -> showAddEditDialog(semester, null, -1));
            semMenu.add(addItem);
            courseList.setComponentPopupMenu(semMenu);
            semPanel.setComponentPopupMenu(semMenu);

            // Course context menu
            courseList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        int idx = courseList.locationToIndex(e.getPoint());
                        if (idx >= 0) {
                            courseList.setSelectedIndex(idx);
                            JPopupMenu courseMenu = new JPopupMenu();
                            JMenuItem editItem = new JMenuItem("Edit Course");
                            editItem.addActionListener(ev -> showAddEditDialog(semester, listModel.get(idx), idx));
                            JMenuItem delItem = new JMenuItem("Delete Course");
                            delItem.addActionListener(ev -> controller.deleteCourse(semester, idx));
                            courseMenu.add(editItem);
                            courseMenu.add(delItem);
                            courseMenu.show(courseList, e.getX(), e.getY());
                        }
                    }
                }
            });

            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(semPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
        saveBtn.setBackground(new Color(0,160,120));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> controller.saveChanges(getEditedCourses()));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
        backBtn.setBackground(new Color(220, 53, 69));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> controller.backToDashboard());
        bottomPanel.add(backBtn);
        bottomPanel.add(saveBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Dialog for add/edit
    private void showAddEditDialog(String semester, CourseModel.CourseInfo course, int idx) {
        JTextField codeField = new JTextField(course != null ? course.code : "");
        JTextField titleField = new JTextField(course != null ? course.title : "");
        JTextField facultyField = new JTextField(course != null ? course.faculty : "");
        JPanel panel = new JPanel(new GridLayout(0,1,8,8));
        panel.add(new JLabel("Course Code:")); panel.add(codeField);
        panel.add(new JLabel("Course Title:")); panel.add(titleField);
        panel.add(new JLabel("Faculty Members:")); panel.add(facultyField);
        int result = JOptionPane.showConfirmDialog(this, panel, (course==null?"Add Course":"Edit Course"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            CourseModel.CourseInfo newCourse = new CourseModel.CourseInfo(codeField.getText().trim(), titleField.getText().trim(), facultyField.getText().trim());
            if (course == null) controller.addCourse(semester, newCourse);
            else controller.editCourse(semester, idx, newCourse);
        }
    }

    // Helper to get all edited courses from lists
    public LinkedHashMap<String, List<CourseModel.CourseInfo>> getEditedCourses() {
        LinkedHashMap<String, List<CourseModel.CourseInfo>> map = new LinkedHashMap<>();
        for (String semester : semesterLists.keySet()) {
            JList<CourseModel.CourseInfo> list = semesterLists.get(semester);
            List<CourseModel.CourseInfo> data = new java.util.ArrayList<>();
            for (int i = 0; i < list.getModel().getSize(); i++) {
                CourseModel.CourseInfo c = list.getModel().getElementAt(i);
                if (c.code != null && !c.code.isEmpty() && c.title != null && !c.title.isEmpty())
                    data.add(c);
            }
            map.put(semester, data);
        }
        return map;
    }

    // For controller to refresh the view after changes
    public void refresh(LinkedHashMap<String, List<CourseModel.CourseInfo>> offeredCourses) {
        getContentPane().removeAll();
        semesterLists.clear();
        mainPanel.removeAll();
        for (String semester : offeredCourses.keySet()) {
            JPanel semPanel = new JPanel(new BorderLayout());
            semPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,160,120), 2), semester, TitledBorder.LEFT, TitledBorder.TOP, new Font("Century Gothic", Font.BOLD, 18), new Color(0,160,120)));
            semPanel.setBackground(Color.WHITE);
            DefaultListModel<CourseModel.CourseInfo> listModel = new DefaultListModel<>();
            for (CourseModel.CourseInfo c : offeredCourses.get(semester)) listModel.addElement(c);
            JList<CourseModel.CourseInfo> courseList = new JList<>(listModel);
            courseList.setCellRenderer(new CourseCellRenderer());
            courseList.setFont(new Font("Arial", Font.PLAIN, 16));
            courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            semesterLists.put(semester, courseList);
            JScrollPane listScroll = new JScrollPane(courseList);
            listScroll.setPreferredSize(new Dimension(800, 120));
            semPanel.add(listScroll, BorderLayout.CENTER);
            // Context menu
            JPopupMenu semMenu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem("Add Course");
            addItem.addActionListener(e -> showAddEditDialog(semester, null, -1));
            semMenu.add(addItem);
            courseList.setComponentPopupMenu(semMenu);
            semPanel.setComponentPopupMenu(semMenu);
            // Course context menu
            courseList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        int idx = courseList.locationToIndex(e.getPoint());
                        if (idx >= 0) {
                            courseList.setSelectedIndex(idx);
                            JPopupMenu courseMenu = new JPopupMenu();
                            JMenuItem editItem = new JMenuItem("Edit Course");
                            editItem.addActionListener(ev -> showAddEditDialog(semester, listModel.get(idx), idx));
                            JMenuItem delItem = new JMenuItem("Delete Course");
                            delItem.addActionListener(ev -> controller.deleteCourse(semester, idx));
                            courseMenu.add(editItem);
                            courseMenu.add(delItem);
                            courseMenu.show(courseList, e.getX(), e.getY());
                        }
                    }
                }
            });
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(semPanel);
        }
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
        saveBtn.setBackground(new Color(0,160,120));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> controller.saveChanges(getEditedCourses()));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
        backBtn.setBackground(new Color(220, 53, 69));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> controller.backToDashboard());
        bottomPanel.add(backBtn);
        bottomPanel.add(saveBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    // Custom cell renderer for beautiful course display
    static class CourseCellRenderer extends JLabel implements ListCellRenderer<CourseModel.CourseInfo> {
        public Component getListCellRendererComponent(JList<? extends CourseModel.CourseInfo> list, CourseModel.CourseInfo value, int index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setText("<html><b>" + value.code + "</b> - " + value.title + "<br><span style='color:#555;'>" + value.faculty + "</span></html>");
            setBackground(isSelected ? new Color(200,230,255) : Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            return this;
        }
    }
} 