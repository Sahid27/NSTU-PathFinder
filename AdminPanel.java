import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminPanel extends JFrame {
    private List<Connection> connections;
    private DefaultListModel<String> constructionListModel;
    private DefaultListModel<String> unsafeListModel;
    private JList<String> constructionList;
    private JList<String> unsafeList;
    private Runnable onUpdateCallback;

    public AdminPanel(List<Connection> connections, Runnable onUpdateCallback) {
        this.connections = connections;
        this.onUpdateCallback = onUpdateCallback;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Panel - Path Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Construction tab
        JPanel constructionPanel = createConstructionPanel();
        tabbedPane.addTab("Construction Updates", constructionPanel);

        // Safety tab
        JPanel safetyPanel = createSafetyPanel();
        tabbedPane.addTab("Path Safety", safetyPanel);

        add(tabbedPane);
    }

    private JPanel createConstructionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Construction Management"));

        // Construction list
        constructionListModel = new DefaultListModel<>();
        constructionList = new JList<>(constructionListModel);
        constructionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Update list with current construction paths
        updateConstructionList();

        JScrollPane scrollPane = new JScrollPane(constructionList);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addConstructionButton = new JButton("Block Path for Construction");
        addConstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addConstructionPath();
            }
        });

        JButton removeConstructionButton = new JButton("Remove Construction Block");
        removeConstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeConstructionPath();
            }
        });

        JButton clearAllButton = new JButton("Clear All Construction");
        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllConstruction();
            }
        });

        buttonPanel.add(addConstructionButton);
        buttonPanel.add(removeConstructionButton);
        buttonPanel.add(clearAllButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Info label
        JLabel infoLabel = new JLabel("Paths under construction will be blocked from all routing calculations.");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
        panel.add(infoLabel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createSafetyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Path Safety Management"));

        // Unsafe paths list
        unsafeListModel = new DefaultListModel<>();
        unsafeList = new JList<>(unsafeListModel);
        unsafeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateUnsafeList();

        JScrollPane scrollPane = new JScrollPane(unsafeList);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton markUnsafeButton = new JButton("Mark Path as Unsafe");
        markUnsafeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markPathUnsafe();
            }
        });

        JButton markSafeButton = new JButton("Mark Path as Safe");
        markSafeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markPathSafe();
            }
        });

        buttonPanel.add(markUnsafeButton);
        buttonPanel.add(markSafeButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Info label
        JLabel infoLabel = new JLabel("Unsafe paths will be avoided in Secure Mode routing.");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
        panel.add(infoLabel, BorderLayout.NORTH);

        return panel;
    }

    private void addConstructionPath() {
        String[] pathOptions = connections.stream()
                .filter(c -> !c.isUnderConstruction())
                .map(Connection::toString)
                .toArray(String[]::new);

        if (pathOptions.length == 0) {
            JOptionPane.showMessageDialog(this, "All paths are already under construction!",
                    "No Available Paths", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this, "Select path to block for construction:", "Block Path",
                JOptionPane.QUESTION_MESSAGE, null, pathOptions, pathOptions[0]);

        if (selected != null) {
            for (Connection conn : connections) {
                if (conn.toString().equals(selected)) {
                    conn.setUnderConstruction(true);
                    break;
                }
            }
            updateConstructionList();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        }
    }

    private void removeConstructionPath() {
        String selected = constructionList.getSelectedValue();
        if (selected != null) {
            for (Connection conn : connections) {
                if (conn.toString().equals(selected)) {
                    conn.setUnderConstruction(false);
                    break;
                }
            }
            updateConstructionList();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a path to remove from construction.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllConstruction() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all construction blocks?",
                "Confirm Clear All", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            for (Connection conn : connections) {
                conn.setUnderConstruction(false);
            }
            updateConstructionList();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        }
    }

    private void markPathUnsafe() {
        String[] pathOptions = connections.stream()
                .filter(Connection::isSafe)
                .map(Connection::toString)
                .toArray(String[]::new);

        if (pathOptions.length == 0) {
            JOptionPane.showMessageDialog(this, "All paths are already marked as unsafe!",
                    "No Safe Paths", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this, "Select path to mark as unsafe:", "Mark Unsafe",
                JOptionPane.QUESTION_MESSAGE, null, pathOptions, pathOptions[0]);

        if (selected != null) {
            for (Connection conn : connections) {
                if (conn.toString().equals(selected)) {
                    conn.setSafe(false);
                    break;
                }
            }
            updateUnsafeList();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        }
    }

    private void markPathSafe() {
        String selected = unsafeList.getSelectedValue();
        if (selected != null) {
            for (Connection conn : connections) {
                if (conn.toString().equals(selected)) {
                    conn.setSafe(true);
                    break;
                }
            }
            updateUnsafeList();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a path to mark as safe.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateConstructionList() {
        constructionListModel.clear();
        for (Connection conn : connections) {
            if (conn.isUnderConstruction()) {
                constructionListModel.addElement(conn.toString());
            }
        }
    }

    private void updateUnsafeList() {
        unsafeListModel.clear();
        for (Connection conn : connections) {
            if (!conn.isSafe()) {
                unsafeListModel.addElement(conn.toString());
            }
        }
    }
}