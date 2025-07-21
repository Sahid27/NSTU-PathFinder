import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;

public class NSTUPathfinder extends JFrame {
    // Constants
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final String ADMIN_PASSWORD = "1234";

    // Data structures
    private List<Node> nodes = new ArrayList<Node>();
    private List<Connection> connections = new ArrayList<Connection>();
    private List<MapLabel> labels = new ArrayList<MapLabel>();
    private List<PathResult> allPaths = new ArrayList<PathResult>();

    // State variables
    private int startNodeIndex = -1;
    private int endNodeIndex = -1;
    private boolean secureMode = false;
    private Set<String> constructionPaths = new HashSet<String>();

    // UI components
    private MapPanel mapPanel;
    private JButton findPathButton;
    private JButton secureModeButton;
    private JButton adminButton;
    private JButton resetButton;
    private JButton helpButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton resetViewButton;
    private JLabel statusLabel;
    private JLabel coordinateLabel;
    private JLabel modeLabel;
    private JLabel zoomLabel;
    private JPanel pathInfoPanel;

    // Colors
    private final Color backgroundColor = new Color(102, 175, 38);
    private final Color nodeColor = new Color(12, 12, 12);
    private final Color pathColor = new Color(255, 255, 255);
    private final Color shortestPathColor = new Color(210, 30, 30);
    private final Color secondPathColor = new Color(255, 165, 0);
    private final Color thirdPathColor = new Color(255, 255, 0);
    private final Color highlightColor = new Color(210, 30, 30);
    private final Color constructionColor = new Color(255, 0, 0);
    private final Color unsafeColor = new Color(255, 100, 100);

    // Button colors
    private final Color primaryButtonColor = new Color(70, 130, 180);
    private final Color secondaryButtonColor = new Color(60, 179, 113);
    private final Color dangerButtonColor = new Color(220, 20, 60);
    private final Color warningButtonColor = new Color(255, 140, 0);
    private final Color infoButtonColor = new Color(106, 90, 205);

    public NSTUPathfinder() {
        setTitle("NSTU Campus Pathfinder - Enhanced Zoomable System");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeData();
        createUI();
        setupLayout();
    }

    private void initializeData() {
        // Initialize all nodes with complete data
        nodes.add(new Node(440, 407, "0", "Central Hub", true));
        nodes.add(new Node(72, 92, "1", "North Gate", true));
        nodes.add(new Node(72, 388, "2", "West Gate", true));
        nodes.add(new Node(107, 407, "3", "Staff Quarter", true));
        nodes.add(new Node(107, 456, "4", "Garage", false));
        nodes.add(new Node(107, 508, "5", "Teachers Dormitory", true));
        nodes.add(new Node(107, 540, "6", "Bangobondhu Hall", true));
        nodes.add(new Node(143, 540, "7", "Proshanti Park", true));
        nodes.add(new Node(143, 595, "8", "Auditorium", true));
        nodes.add(new Node(143, 672, "9", "Bangomata Hall", true));
        nodes.add(new Node(269, 92, "10", "Temple", true));
        nodes.add(new Node(300, 170, "11", "Khadiza", true));
        nodes.add(new Node(330, 170, "12", "Building 12", true));
        nodes.add(new Node(375, 170, "13", "Building 13", true));
        nodes.add(new Node(447, 170, "14", "Admin Building", true));
        nodes.add(new Node(523, 170, "15", "VC Office", true));
        nodes.add(new Node(568, 140, "16", "Field West", false));
        nodes.add(new Node(600, 100, "17", "Pocket Gate", false));
        nodes.add(new Node(523, 209, "18", "Mosque", true));
        nodes.add(new Node(580, 220, "19", "Cafeteria", true));
        nodes.add(new Node(610, 330, "20", "Building 20", true));
        nodes.add(new Node(577, 368, "21", "Building 21", true));
        nodes.add(new Node(523, 370, "22", "Library", true));
        nodes.add(new Node(523, 270, "23", "Building 3", true));
        nodes.add(new Node(565, 270, "24", "BNCC", true));
        nodes.add(new Node(300, 350, "25", "Nil Dighi North", true));
        nodes.add(new Node(300, 407, "26", "Shanti Niketon", true));
        nodes.add(new Node(280, 407, "27", "Medical Center", true));
        nodes.add(new Node(247, 407, "28", "Teachers Dormitory 2", true));
        nodes.add(new Node(280, 436, "29", "Mosque 2", true));
        nodes.add(new Node(280, 460, "30", "Field", false));
        nodes.add(new Node(280, 487, "31", "Central Area", true));
        nodes.add(new Node(310, 487, "32", "Junction 32", true));
        nodes.add(new Node(280, 522, "33", "Buildings Area", true));
        nodes.add(new Node(290, 545, "34", "Dormitory 34", true));
        nodes.add(new Node(290, 545, "35", "Dormitory 35", true));
        nodes.add(new Node(290, 685, "36", "Dormitory 36", true));
        nodes.add(new Node(387, 685, "37", "Dormitory 37", true));
        nodes.add(new Node(387, 545, "38", "Dormitory 38", true));
        nodes.add(new Node(345, 545, "39", "Dormitory 39", true));
        nodes.add(new Node(348, 522, "40", "Dormitory 40", true));
        nodes.add(new Node(591, 522, "41", "Dormitory 41", true));
        nodes.add(new Node(591, 634, "42", "Dormitory 42", true));
        nodes.add(new Node(550, 634, "43", "Dormitory 43", true));
        nodes.add(new Node(590, 711, "44", "Dormitory 44", true));
        nodes.add(new Node(463, 711, "45", "Dormitory 45", true));
        nodes.add(new Node(463, 580, "46", "Dormitory 46", true));
        nodes.add(new Node(415, 734, "47", "Dormitory 47", true));
        nodes.add(new Node(481, 580, "48", "Dormitory 48", true));
        nodes.add(new Node(481, 552, "49", "Dormitory 49", true));
        nodes.add(new Node(561, 552, "50", "Dormitory 50", true));
        nodes.add(new Node(561, 605, "51", "Dormitory 51", true));
        nodes.add(new Node(481, 605, "52", "Dormitory 52", true));
        nodes.add(new Node(363, 450, "53", "Building 53", true));
        nodes.add(new Node(440, 444, "54", "Building 54", true));
        nodes.add(new Node(490, 444, "55", "Building 55", true));
        nodes.add(new Node(527, 441, "56", "Building 56", true));
        nodes.add(new Node(553, 448, "57", "Building 57", true));
        nodes.add(new Node(555, 468, "58", "Building 58", true));
        nodes.add(new Node(590, 475, "59", "Building 59", true));
        nodes.add(new Node(594, 458, "60", "Building 60", true));
        nodes.add(new Node(595, 443, "61", "Building 61", true));
        nodes.add(new Node(587, 437, "62", "Building 62", true));
        nodes.add(new Node(576, 429, "63", "Building 63", true));
        nodes.add(new Node(627, 411, "64", "Main Gate Area", true));
        nodes.add(new Node(624, 396, "65", "Gate 65", true));
        nodes.add(new Node(624, 461, "66", "Building 66", true));
        nodes.add(new Node(621, 483, "67", "Building 67", true));
        nodes.add(new Node(656, 412, "68", "Main Gate", true));
        nodes.add(new Node(652, 403, "69", "Gate Area", true));
        nodes.add(new Node(523, 407, "70", "Library Junction", true));

        // Initialize all connections (same as before)
        addConnection(0, 70, "Main Path", 1.0, true);
        addConnection(1, 10, "Main Path", 5.0, true);
        addConnection(1, 2, "Temple Path", 7.0, false);
        addConnection(2, 3, "Khadiza Path", 0.8, true);
        addConnection(3, 4, "Salam Path", 1.2, false);
        addConnection(4, 5, "Cafeteria Path", 1.5, true);
        addConnection(5, 6, "East Path", 1.0, true);
        addConnection(6, 7, "Proshanti Path", 0.7, true);
        addConnection(7, 8, "Auditorium Path", 1.3, true);
        addConnection(8, 9, "Eastern path", 2.5, true);
        addConnection(10, 11, "Buildings Path", 1.8, true);
        addConnection(11, 12, "Building", 0.5, true);
        addConnection(12, 13, "Building", 1.0, true);
        addConnection(13, 14, "Admin to VC Path", 1.5, true);
        addConnection(14, 15, "VC Bangla Path", 1.6, true);
        addConnection(15, 16, "West path", 1.2, false);
        addConnection(16, 17, "Garage Path", 1.4, false);
        addConnection(11, 12, "Staff Quarter Path", 0.5, true);
        addConnection(12, 13, "Bangobondhu Path", 1.0, true);
        addConnection(13, 14, "Bangomata Path", 1.5, true);
        addConnection(14, 25, "Bangomata Path", 4.5, true);
        addConnection(15, 16, "Field West Path", 1.0, false);
        addConnection(16, 17, "Field Entrance", 1.5, false);
        addConnection(16, 19, "Field Entrance", 2.0, false);
        addConnection(15, 18, "Mosque Path", 1.0, true);
        addConnection(18, 19, "Central Path", 1.3, true);
        addConnection(18, 23, "Building 3 Path", 1.5, true);
        addConnection(19, 20, "Buildings", 3.0, true);
        addConnection(20, 21, "Library Path", 1.0, true);
        addConnection(21, 22, "Library Entrance", 1.2, true);
        addConnection(21, 63, "Building 2 Path", 2.5, true);
        addConnection(22, 23, "Building Connection", 2.0, true);
        addConnection(26, 0, "South West Path", 2.8, true);
        addConnection(23, 24, "Nil Dighi West", 1.0, true);
        addConnection(25, 26, "Nil Dighi North", 1.5, true);
        addConnection(26, 27, "Nil Dighi East", 0.5, true);
        addConnection(27, 28, "South East Path", 0.8, true);
        addConnection(27, 29, "Dormitory Link", 0.7, true);
        addConnection(3, 28, "Teachers Dormitory", 3.5, true);
        addConnection(29, 30, "South Path", 0.6, false);
        addConnection(30, 31, "Field", 0.7, false);
        addConnection(31, 32, "Central", 0.7, true);
        addConnection(31, 33, "Buildings", 0.8, true);
        addConnection(7, 33, "Nil Dighi", 2.5, true);
        addConnection(33, 34, "Dormitory", 0.5, true);
        addConnection(33, 40, "Dormitory", 1.5, true);
        addConnection(34, 39, "Dormitory", 1.2, true);
        addConnection(34, 35, "Dormitory", 3.5, true);
        addConnection(35, 36, "Dormitory", 2.0, true);
        addConnection(36, 37, "Dormitory", 2.0, true);
        addConnection(64, 65, "Dormitory", 0.5, true);
        addConnection(46, 48, "Dormitory", 0.5, true);
        addConnection(37, 38, "Dormitory", 3.0, true);
        addConnection(38, 39, "Dormitory", 1.0, true);
        addConnection(39, 40, "Dormitory", 0.5, true);
        addConnection(40, 41, "Dormitory", 5.0, true);
        addConnection(64, 69, "Dormitory", 0.3, true);
        addConnection(41, 42, "Dormitory", 2.0, true);
        addConnection(42, 43, "Dormitory", 1.5, true);
        addConnection(42, 44, "Dormitory", 2.5, true);
        addConnection(44, 45, "Dormitory", 4.0, true);
        addConnection(45, 46, "Dormitory", 1.5, true);
        addConnection(45, 47, "Dormitory", 0.7, true);
        addConnection(37, 47, "Dormitory", 6.0, true);
        addConnection(52, 48, "Dormitory", 0.8, true);
        addConnection(52, 51, "Dormitory", 0.9, true);
        addConnection(48, 49, "Dormitory", 0.7, true);
        addConnection(49, 50, "Dormitory", 2.0, true);
        addConnection(50, 51, "Dormitory", 1.0, true);
        addConnection(0, 22, "Dormitory", 2.0, true);
        addConnection(57, 63, "Dormitory", 0.6, true);
        addConnection(53, 54, "Dormitory", 1.5, true);
        addConnection(54, 55, "Dormitory", 1.0, true);
        addConnection(55, 56, "Dormitory", 0.8, true);
        addConnection(70, 22, "Dormitory", 1.0, true);
        addConnection(63, 62, "Dormitory", 0.5, true);
        addConnection(56, 57, "Dormitory", 0.6, true);
        addConnection(57, 58, "Dormitory", 0.5, true);
        addConnection(58, 59, "Dormitory", 0.8, true);
        addConnection(60, 66, "Dormitory", 1.0, true);
        addConnection(59, 41, "Dormitory", 1.5, true);
        addConnection(59, 60, "Dormitory", 0.5, true);
        addConnection(60, 61, "Dormitory", 0.3, true);
        addConnection(59, 67, "Dormitory", 0.6, true);
        addConnection(61, 62, "Dormitory", 0.5, true);
        addConnection(62, 64, "Dormitory", 1.0, true);
        addConnection(61, 68, "Dormitory", 1.5, true);
        addConnection(11, 25, "Dormitory", 4.0, true);
        addConnection(70, 56, "Dormitory", 2.0, true);

        // Initialize all labels (same as before)
        labels.add(new MapLabel("Moina dip", 180, 250));
        labels.add(new MapLabel("Field", 401, 285));
        labels.add(new MapLabel("Temple", 315, 154));
        labels.add(new MapLabel("Khadiza", 363, 155));
        labels.add(new MapLabel("Malek Hall", 427, 157));
        labels.add(new MapLabel("Salam", 500, 150));
        labels.add(new MapLabel("Cafeteria", 590, 220));
        labels.add(new MapLabel("Proshanti park", 617, 317));
        labels.add(new MapLabel("Auditorium", 590, 367));
        labels.add(new MapLabel("Staff Quarter", 115, 395));
        labels.add(new MapLabel("Shanti Niketon", 306, 388));
        labels.add(new MapLabel("Mosque", 225, 436));
        labels.add(new MapLabel("Library", 322, 487));
        labels.add(new MapLabel("3", 370, 470));
        labels.add(new MapLabel("2", 440, 465));
        labels.add(new MapLabel("1", 487, 465));
        labels.add(new MapLabel("0", 570, 457));
        labels.add(new MapLabel("Admin", 535, 487));
        labels.add(new MapLabel("Nil Dighi", 325, 603));
        labels.add(new MapLabel("VC Bangla", 520, 660));
        labels.add(new MapLabel("Teachers Dormitory", 3, 500));
        labels.add(new MapLabel("Teachers Dormitory", 377, 756));
        labels.add(new MapLabel("Bangobondhu Hall", 37, 595));
        labels.add(new MapLabel("Bangomata Hall", 45, 672));
        labels.add(new MapLabel("Garage", 50, 456));
        labels.add(new MapLabel("BNCC", 547, 289));
        labels.add(new MapLabel("Pocket Gate", 617, 98));
        labels.add(new MapLabel("Main Gate", 666, 397));
        labels.add(new MapLabel("Mosque 2", 630, 490));
        labels.add(new MapLabel("Shahid Minar", 635, 460));
        labels.add(new MapLabel("Medical", 220, 397));
        labels.add(new MapLabel("ATM", 600, 390));
    }

    private void addConnection(int fromIndex, int toIndex, String name, double weight, boolean isSafe) {
        if (fromIndex < nodes.size() && toIndex < nodes.size()) {
            connections.add(new Connection(nodes.get(fromIndex), nodes.get(toIndex), name, weight, isSafe));
        }
    }

    private void createUI() {
        // Create styled buttons
        findPathButton = createStyledButton("🔍 Find Paths", primaryButtonColor);
        findPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPathfinding();
            }
        });

        secureModeButton = createStyledButton("🔒 Normal Mode", secondaryButtonColor);
        secureModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleSecureMode();
            }
        });

        adminButton = createStyledButton("⚙️ Admin Panel", warningButtonColor);
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdminPanel();
            }
        });

        resetButton = createStyledButton("🔄 Reset", dangerButtonColor);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPathfinding();
            }
        });

        helpButton = createStyledButton("❓ Help", new Color(128, 128, 128));
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        // Zoom control buttons
        zoomInButton = createStyledButton("🔍+ Zoom In", infoButtonColor);
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.zoomIn();
            }
        });

        zoomOutButton = createStyledButton("🔍- Zoom Out", infoButtonColor);
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.zoomOut();
            }
        });

        resetViewButton = createStyledButton("🎯 Reset View", infoButtonColor);
        resetViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.resetView();
            }
        });

        // Labels with better styling
        statusLabel = new JLabel("Click start node, then end node");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        coordinateLabel = new JLabel("Coordinates will appear here");
        coordinateLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        coordinateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        modeLabel = new JLabel("Mode: Normal (All paths)");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        zoomLabel = new JLabel("Zoom: 100%");
        zoomLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Path info panel with better styling
        pathInfoPanel = new JPanel();
        pathInfoPanel.setLayout(new BoxLayout(pathInfoPanel, BoxLayout.Y_AXIS));
        pathInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Path Information",
                0, 0, new Font("Arial", Font.BOLD, 12)));
        pathInfoPanel.setBackground(Color.WHITE);

        // Map panel
        mapPanel = new MapPanel();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(140, 30));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Control panel with better organization
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(400, HEIGHT));
        controlPanel.setBackground(new Color(240, 240, 240));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("NSTU Pathfinder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);

        // Button panel with better layout
        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(findPathButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(secureModeButton);
        buttonPanel.add(adminButton);
        buttonPanel.add(helpButton);

        // Separator
        JLabel separator = new JLabel("─── Map Controls ───");
        separator.setHorizontalAlignment(SwingConstants.CENTER);
        separator.setFont(new Font("Arial", Font.BOLD, 10));
        separator.setForeground(Color.GRAY);
        buttonPanel.add(separator);

        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(resetViewButton);

        // Status panel with better styling
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Status",
                0, 0, new Font("Arial", Font.BOLD, 12)));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.add(statusLabel);
        statusPanel.add(coordinateLabel);
        statusPanel.add(modeLabel);
        statusPanel.add(zoomLabel);

        // Legend panel
        JPanel legendPanel = createLegendPanel();

        controlPanel.add(titlePanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(legendPanel, BorderLayout.CENTER);
        bottomPanel.add(new JScrollPane(pathInfoPanel), BorderLayout.SOUTH);

        controlPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Legend & Controls",
                0, 0, new Font("Arial", Font.BOLD, 12)));
        legendPanel.setBackground(new Color(240, 240, 240));

        // Create legend items
        legendPanel.add(createLegendItem("●", shortestPathColor, "1st Shortest Path"));
        legendPanel.add(createLegendItem("●", secondPathColor, "2nd Shortest Path"));
        legendPanel.add(createLegendItem("●", thirdPathColor, "3rd Shortest Path"));
        legendPanel.add(createLegendItem("●", constructionColor, "Construction/Blocked"));
        legendPanel.add(createLegendItem("●", unsafeColor, "Unsafe Path"));

        // Add control instructions
        JLabel controlsTitle = new JLabel("Map Controls:");
        controlsTitle.setFont(new Font("Arial", Font.BOLD, 10));
        legendPanel.add(controlsTitle);

        JLabel mouseWheelLabel = new JLabel("• Mouse wheel: Zoom in/out");
        mouseWheelLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        legendPanel.add(mouseWheelLabel);

        JLabel dragLabel = new JLabel("• Click & drag: Move map");
        dragLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        legendPanel.add(dragLabel);

        return legendPanel;
    }

    private JPanel createLegendItem(String symbol, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        item.setBackground(new Color(240, 240, 240));

        JLabel colorLabel = new JLabel(symbol);
        colorLabel.setForeground(color);
        colorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 9));

        item.add(colorLabel);
        item.add(textLabel);

        return item;
    }

    private void showHelp() {
        String helpText = "NSTU Campus Pathfinder Help\n\n" +
                "Navigation:\n" +
                "• Mouse wheel: Zoom in/out\n" +
                "• Click & drag: Move/pan the map\n" +
                "• Zoom buttons: Precise zoom control\n" +
                "• Reset View: Return to original view\n\n" +
                "Pathfinding:\n" +
                "1. Click on a node to select start point\n" +
                "2. Click on another node to select end point\n" +
                "3. The system will show up to 3 shortest paths\n\n" +
                "Features:\n" +
                "• Red path: Shortest route\n" +
                "• Orange path: 2nd shortest route\n" +
                "• Yellow path: 3rd shortest route\n" +
                "• Secure Mode: Shows only safe paths\n" +
                "• Admin Panel: Manage construction blocks\n\n" +
                "Colors:\n" +
                "• Blue nodes: Selected start/end points\n" +
                "• Red circle: Unsafe locations\n" +
                "• Dashed red: Construction/blocked paths";

        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    // Rest of the methods remain the same as previous version...
    private void resetPathfinding() {
        startNodeIndex = -1;
        endNodeIndex = -1;
        allPaths.clear();
        statusLabel.setText("Click start node, then end node");
        updatePathInfoDisplay();
        mapPanel.repaint();
    }

    private void toggleSecureMode() {
        secureMode = !secureMode;
        if (secureMode) {
            secureModeButton.setText("🔓 Secure Mode ON");
            secureModeButton.setBackground(new Color(220, 20, 60));
            modeLabel.setText("Mode: Secure (Safe paths only)");
            modeLabel.setForeground(new Color(0, 150, 0));
        } else {
            secureModeButton.setText("🔒 Normal Mode");
            secureModeButton.setBackground(secondaryButtonColor);
            modeLabel.setText("Mode: Normal (All paths)");
            modeLabel.setForeground(Color.BLACK);
        }

        if (startNodeIndex != -1 && endNodeIndex != -1) {
            findPaths();
        }
    }

    private void showAdminPanel() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, passwordField,
                "Enter Admin Password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if (!ADMIN_PASSWORD.equals(password)) {
                JOptionPane.showMessageDialog(this, "Incorrect password!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            return;
        }

        JFrame adminFrame = new JFrame("Admin Panel - Construction Management");
        adminFrame.setSize(700, 600);
        adminFrame.setLocationRelativeTo(this);

        JTabbedPane tabs = new JTabbedPane();

        JPanel constructionPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> constructionModel = new DefaultListModel<String>();
        JList<String> constructionList = new JList<String>(constructionModel);

        for (String path : constructionPaths) {
            constructionModel.addElement(path);
        }

        JPanel constructionButtons = new JPanel(new FlowLayout());
        JButton addConstruction = createStyledButton("🚧 Block Path", dangerButtonColor);
        JButton removeConstruction = createStyledButton("✅ Unblock Path", secondaryButtonColor);
        JButton clearAll = createStyledButton("🗑️ Clear All", warningButtonColor);

        addConstruction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] options = new String[connections.size()];
                for (int i = 0; i < connections.size(); i++) {
                    Connection c = connections.get(i);
                    options[i] = c.from.name + " → " + c.to.name + " (" + c.name + ")";
                }

                String selected = (String) JOptionPane.showInputDialog(adminFrame,
                        "Select path to block:", "Block Construction",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (selected != null) {
                    constructionPaths.add(selected);
                    constructionModel.addElement(selected);
                    mapPanel.repaint();

                    if (startNodeIndex != -1 && endNodeIndex != -1) {
                        findPaths();
                    }
                }
            }
        });

        removeConstruction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = constructionList.getSelectedValue();
                if (selected != null) {
                    constructionPaths.remove(selected);
                    constructionModel.removeElement(selected);
                    mapPanel.repaint();

                    if (startNodeIndex != -1 && endNodeIndex != -1) {
                        findPaths();
                    }
                }
            }
        });

        clearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(adminFrame,
                        "Are you sure you want to clear all blocked paths?",
                        "Confirm Clear All", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    constructionPaths.clear();
                    constructionModel.clear();
                    mapPanel.repaint();

                    if (startNodeIndex != -1 && endNodeIndex != -1) {
                        findPaths();
                    }
                }
            }
        });

        constructionButtons.add(addConstruction);
        constructionButtons.add(removeConstruction);
        constructionButtons.add(clearAll);

        constructionPanel.add(new JScrollPane(constructionList), BorderLayout.CENTER);
        constructionPanel.add(constructionButtons, BorderLayout.SOUTH);

        tabs.addTab("Construction Management", constructionPanel);

        adminFrame.add(tabs);
        adminFrame.setVisible(true);
    }

    private void findPaths() {
        allPaths = findMultipleShortestPaths(startNodeIndex, endNodeIndex);

        if (allPaths.isEmpty()) {
            statusLabel.setText("No paths found!");
        } else {
            statusLabel.setText("Found " + allPaths.size() + " path(s)! Shortest: " +
                    String.format("%.2f", allPaths.get(0).totalWeight));
        }

        updatePathInfoDisplay();
        mapPanel.repaint();
    }

    private List<PathResult> findMultipleShortestPaths(int startIdx, int endIdx) {
        List<PathResult> results = new ArrayList<PathResult>();

        if (startIdx < 0 || endIdx < 0 || startIdx >= nodes.size() || endIdx >= nodes.size()) {
            return results;
        }

        Node startNode = nodes.get(startIdx);
        Node endNode = nodes.get(endIdx);

        Set<List<Node>> foundPaths = new HashSet<List<Node>>();

        for (int attempt = 0; attempt < 3; attempt++) {
            Map<Node, List<Node>> graph = new HashMap<Node, List<Node>>();
            Map<String, Double> weights = new HashMap<String, Double>();
            Set<String> excludedEdges = new HashSet<String>();

            if (attempt > 0 && !results.isEmpty()) {
                PathResult lastPath = results.get(results.size() - 1);
                if (lastPath.path.size() > 1) {
                    int excludeIndex = attempt % (lastPath.path.size() - 1);
                    Node from = lastPath.path.get(excludeIndex);
                    Node to = lastPath.path.get(excludeIndex + 1);
                    excludedEdges.add(from.toString() + "-" + to.toString());
                    excludedEdges.add(to.toString() + "-" + from.toString());
                }
            }

            for (Connection conn : connections) {
                String pathName = conn.from.name + " → " + conn.to.name + " (" + conn.name + ")";
                if (constructionPaths.contains(pathName)) continue;

                if (secureMode && !conn.isSafe) continue;

                String edgeKey = conn.from.toString() + "-" + conn.to.toString();
                String reverseEdgeKey = conn.to.toString() + "-" + conn.from.toString();
                if (excludedEdges.contains(edgeKey) || excludedEdges.contains(reverseEdgeKey)) continue;

                if (!graph.containsKey(conn.from)) {
                    graph.put(conn.from, new ArrayList<Node>());
                }
                graph.get(conn.from).add(conn.to);

                if (!graph.containsKey(conn.to)) {
                    graph.put(conn.to, new ArrayList<Node>());
                }
                graph.get(conn.to).add(conn.from);

                weights.put(edgeKey, conn.weight);
                weights.put(reverseEdgeKey, conn.weight);
            }

            PathResult path = dijkstraPath(graph, weights, startNode, endNode);
            if (path != null && !path.path.isEmpty()) {
                boolean isDifferent = true;
                for (PathResult existingPath : results) {
                    if (pathsSimilar(path.path, existingPath.path)) {
                        isDifferent = false;
                        break;
                    }
                }

                if (isDifferent) {
                    results.add(path);
                }
            }

            if (results.size() >= 3) break;
        }

        Collections.sort(results, new Comparator<PathResult>() {
            public int compare(PathResult a, PathResult b) {
                return Double.compare(a.totalWeight, b.totalWeight);
            }
        });

        return results;
    }

    private boolean pathsSimilar(List<Node> path1, List<Node> path2) {
        if (path1.size() != path2.size()) return false;

        int commonNodes = 0;
        for (Node node : path1) {
            if (path2.contains(node)) {
                commonNodes++;
            }
        }

        return (double) commonNodes / path1.size() > 0.7;
    }

    private PathResult dijkstraPath(Map<Node, List<Node>> graph, Map<String, Double> weights,
                                    Node start, Node end) {
        Map<Node, Double> distances = new HashMap<Node, Double>();
        Map<Node, Node> previousNodes = new HashMap<Node, Node>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<NodeDistance>(new Comparator<NodeDistance>() {
            public int compare(NodeDistance a, NodeDistance b) {
                return Double.compare(a.distance, b.distance);
            }
        });
        Set<Node> visited = new HashSet<Node>();

        for (Node node : nodes) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(new NodeDistance(start, 0.0));

        while (!queue.isEmpty() && !visited.contains(end)) {
            NodeDistance current = queue.poll();
            Node currentNode = current.node;

            if (visited.contains(currentNode)) {
                continue;
            }

            visited.add(currentNode);

            if (graph.containsKey(currentNode)) {
                for (Node neighbor : graph.get(currentNode)) {
                    if (visited.contains(neighbor)) {
                        continue;
                    }

                    String edgeKey = currentNode.toString() + "-" + neighbor.toString();
                    double weight = weights.containsKey(edgeKey) ? weights.get(edgeKey) : 1.0;
                    double totalDistance = distances.get(currentNode) + weight;

                    if (totalDistance < distances.get(neighbor)) {
                        distances.put(neighbor, totalDistance);
                        previousNodes.put(neighbor, currentNode);
                        queue.add(new NodeDistance(neighbor, totalDistance));
                    }
                }
            }
        }

        List<Node> path = new ArrayList<Node>();
        Node current = end;

        if (!previousNodes.containsKey(end)) {
            return null;
        }

        double totalWeight = distances.get(end);

        while (current != null) {
            path.add(current);
            current = previousNodes.get(current);
            if (current == start) {
                path.add(start);
                break;
            }
        }

        Collections.reverse(path);
        return new PathResult(path, totalWeight);
    }

    private void updatePathInfoDisplay() {
        pathInfoPanel.removeAll();

        if (allPaths.isEmpty()) {
            JLabel noPathLabel = new JLabel("No paths found");
            noPathLabel.setHorizontalAlignment(SwingConstants.CENTER);
            pathInfoPanel.add(noPathLabel);
        } else {
            for (int i = 0; i < allPaths.size(); i++) {
                PathResult path = allPaths.get(i);
                String pathType = (i == 0) ? "1st Shortest" : (i == 1) ? "2nd Shortest" : "3rd Shortest";

                JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                pathPanel.setBackground(Color.WHITE);

                JLabel colorIndicator = new JLabel("●");
                colorIndicator.setForeground(i == 0 ? shortestPathColor : i == 1 ? secondPathColor : thirdPathColor);
                colorIndicator.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

                JLabel pathInfo = new JLabel("<html><b>" + pathType + " Path:</b><br/>" +
                        (path.path.size() - 1) + " segments, Weight: " +
                        String.format("%.2f", path.totalWeight) + "</html>");
                pathInfo.setFont(new Font("Arial", Font.PLAIN, 11));

                pathPanel.add(colorIndicator);
                pathPanel.add(pathInfo);
                pathInfoPanel.add(pathPanel);

                StringBuilder pathDetails = new StringBuilder("<html><small><i>Route: ");
                for (int j = 0; j < Math.min(path.path.size(), 4); j++) {
                    pathDetails.append(path.path.get(j).name);
                    if (j < Math.min(path.path.size(), 4) - 1) {
                        pathDetails.append(" → ");
                    }
                }
                if (path.path.size() > 4) {
                    pathDetails.append(" → ... → ").append(path.path.get(path.path.size() - 1).name);
                }
                pathDetails.append("</i></small></html>");

                JLabel detailsLabel = new JLabel(pathDetails.toString());
                detailsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                pathInfoPanel.add(detailsLabel);

                if (i < allPaths.size() - 1) {
                    pathInfoPanel.add(new JLabel(" "));
                }
            }
        }

        pathInfoPanel.revalidate();
        pathInfoPanel.repaint();
    }

    // Inner classes
    class Node {
        int x, y;
        String id, name;
        boolean isSafe;

        Node(int x, int y, String id, String name, boolean isSafe) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.name = name;
            this.isSafe = isSafe;
        }

        public String toString() {
            return "Node(" + x + "," + y + ")";
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    class Connection {
        Node from, to;
        String name;
        double weight;
        boolean isSafe;

        Connection(Node from, Node to, String name, double weight, boolean isSafe) {
            this.from = from;
            this.to = to;
            this.name = name;
            this.weight = weight;
            this.isSafe = isSafe;
        }
    }

    class PathResult {
        List<Node> path;
        double totalWeight;

        PathResult(List<Node> path, double totalWeight) {
            this.path = new ArrayList<Node>(path);
            this.totalWeight = totalWeight;
        }
    }

    class MapLabel {
        String text;
        int x, y;

        MapLabel(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    class NodeDistance {
        Node node;
        double distance;

        NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    class MapPanel extends JPanel {
        // Zoom and pan variables
        private double zoomFactor = 1.0;
        private double panX = 0;
        private double panY = 0;
        private Point lastPanPoint;
        private boolean isDragging = false;

        // Zoom limits
        private static final double MIN_ZOOM = 0.5;
        private static final double MAX_ZOOM = 3.0;
        private static final double ZOOM_STEP = 0.2;

        public MapPanel() {
            // Mouse listeners for clicking nodes
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (!isDragging) {
                        Point2D transformedPoint = transformPoint(e.getX(), e.getY());
                        coordinateLabel.setText("Coordinates: (" + (int)transformedPoint.getX() +
                                ", " + (int)transformedPoint.getY() + ")");
                        handleMouseClick((int)transformedPoint.getX(), (int)transformedPoint.getY());
                    }
                }

                public void mousePressed(MouseEvent e) {
                    lastPanPoint = e.getPoint();
                    isDragging = false;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }

                public void mouseReleased(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    isDragging = false;
                }
            });

            // Mouse motion listener for panning
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (lastPanPoint != null) {
                        isDragging = true;
                        int dx = e.getX() - lastPanPoint.x;
                        int dy = e.getY() - lastPanPoint.y;

                        panX += dx;
                        panY += dy;

                        lastPanPoint = e.getPoint();
                        repaint();
                    }
                }
            });

            // Mouse wheel listener for zooming
            addMouseWheelListener(new MouseWheelListener() {
                public void mouseWheelMoved(MouseWheelEvent e) {
                    Point mousePoint = e.getPoint();

                    if (e.getWheelRotation() < 0) {
                        zoomIn(mousePoint);
                    } else {
                        zoomOut(mousePoint);
                    }
                }
            });
        }

        public void zoomIn() {
            zoomIn(new Point(getWidth() / 2, getHeight() / 2));
        }

        public void zoomOut() {
            zoomOut(new Point(getWidth() / 2, getHeight() / 2));
        }

        private void zoomIn(Point center) {
            if (zoomFactor < MAX_ZOOM) {
                double oldZoom = zoomFactor;
                zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);

                // Adjust pan to zoom towards the center point
                double zoomRatio = zoomFactor / oldZoom;
                panX = center.x - (center.x - panX) * zoomRatio;
                panY = center.y - (center.y - panY) * zoomRatio;

                updateZoomLabel();
                repaint();
            }
        }

        private void zoomOut(Point center) {
            if (zoomFactor > MIN_ZOOM) {
                double oldZoom = zoomFactor;
                zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);

                // Adjust pan to zoom towards the center point
                double zoomRatio = zoomFactor / oldZoom;
                panX = center.x - (center.x - panX) * zoomRatio;
                panY = center.y - (center.y - panY) * zoomRatio;

                updateZoomLabel();
                repaint();
            }
        }

        public void resetView() {
            zoomFactor = 1.0;
            panX = 0;
            panY = 0;
            updateZoomLabel();
            repaint();
        }

        private void updateZoomLabel() {
            zoomLabel.setText("Zoom: " + (int)(zoomFactor * 100) + "%");
        }

        private Point2D transformPoint(int screenX, int screenY) {
            // Transform screen coordinates to map coordinates
            double mapX = (screenX - panX) / zoomFactor;
            double mapY = (screenY - panY) / zoomFactor;
            return new Point2D.Double(mapX, mapY);
        }

        private void handleMouseClick(int x, int y) {
            boolean nodeClicked = false;

            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                double distance = Math.sqrt(Math.pow(node.x - x, 2) + Math.pow(node.y - y, 2));

                // Adjust click tolerance based on zoom
                double tolerance = 12 / zoomFactor;

                if (distance <= tolerance) {
                    nodeClicked = true;

                    if (startNodeIndex == -1) {
                        startNodeIndex = i;
                        statusLabel.setText("Start: " + node.name + " - Click end node");
                    } else if (endNodeIndex == -1 && i != startNodeIndex) {
                        endNodeIndex = i;
                        statusLabel.setText("Finding paths...");
                        findPaths();
                    } else {
                        resetPathfinding();
                        startNodeIndex = i;
                        statusLabel.setText("Start: " + node.name + " - Click end node");
                    }

                    repaint();
                    break;
                }
            }

            if (!nodeClicked) {
                statusLabel.setText("Click on a node to select it");
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Save original transform
            AffineTransform originalTransform = g2d.getTransform();

            // Apply zoom and pan transformations
            g2d.translate(panX, panY);
            g2d.scale(zoomFactor, zoomFactor);

            // Background
            g2d.setColor(backgroundColor);
            g2d.fillRect((int)(-panX/zoomFactor), (int)(-panY/zoomFactor),
                    (int)(getWidth()/zoomFactor + Math.abs(panX/zoomFactor)),
                    (int)(getHeight()/zoomFactor + Math.abs(panY/zoomFactor)));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw connections
            for (Connection conn : connections) {
                String pathName = conn.from.name + " → " + conn.to.name + " (" + conn.name + ")";

                if (constructionPaths.contains(pathName)) {
                    g2d.setColor(constructionColor);
                    g2d.setStroke(new BasicStroke((float)(3/zoomFactor), BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND, 0, new float[]{(float)(5/zoomFactor)}, 0));
                } else if (!conn.isSafe) {
                    g2d.setColor(unsafeColor);
                    g2d.setStroke(new BasicStroke((float)(2/zoomFactor)));
                } else {
                    g2d.setColor(pathColor);
                    g2d.setStroke(new BasicStroke((float)(1/zoomFactor)));
                }

                g2d.drawLine(conn.from.x, conn.from.y, conn.to.x, conn.to.y);
            }

            // Draw paths with different colors and widths
            for (int pathIndex = 0; pathIndex < allPaths.size(); pathIndex++) {
                PathResult pathResult = allPaths.get(pathIndex);
                List<Node> path = pathResult.path;

                Color pathColor;
                float strokeWidth;

                if (pathIndex == 0) {
                    pathColor = shortestPathColor;
                    strokeWidth = (float)(6/zoomFactor);
                } else if (pathIndex == 1) {
                    pathColor = secondPathColor;
                    strokeWidth = (float)(4/zoomFactor);
                } else {
                    pathColor = thirdPathColor;
                    strokeWidth = (float)(3/zoomFactor);
                }

                g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(pathColor);

                for (int i = 0; i < path.size() - 1; i++) {
                    Node current = path.get(i);
                    Node next = path.get(i + 1);
                    g2d.drawLine(current.x, current.y, next.x, next.y);
                }
            }

            // Draw nodes
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);

                // Node color
                if (startNodeIndex == i || endNodeIndex == i) {
                    g2d.setColor(Color.BLUE);
                } else {
                    boolean isInPath = false;
                    for (PathResult pathResult : allPaths) {
                        if (pathResult.path.contains(node)) {
                            isInPath = true;
                            break;
                        }
                    }
                    g2d.setColor(isInPath ? highlightColor : nodeColor);
                }

                int nodeSize = (int)(12 / zoomFactor);
                g2d.fillOval(node.x - nodeSize/2, node.y - nodeSize/2, nodeSize, nodeSize);

                // Safety indicator
                if (!node.isSafe) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke((float)(2/zoomFactor)));
                    int safetySize = (int)(16 / zoomFactor);
                    g2d.drawOval(node.x - safetySize/2, node.y - safetySize/2, safetySize, safetySize);
                }

                // Node ID (only show if zoom is sufficient)
                if (zoomFactor > 0.8) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, (int)(8 / zoomFactor)));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(node.id);
                    g2d.drawString(node.id, node.x - textWidth/2, node.y + (int)(3/zoomFactor));
                }
            }

            // Draw labels (only show if zoom is sufficient)
            if (zoomFactor > 0.6) {
                g2d.setFont(new Font("Arial", Font.BOLD, (int)(10 / zoomFactor)));
                for (MapLabel label : labels) {
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(label.text);
                    int textHeight = fm.getHeight();

                    // Background
                    g2d.setColor(new Color(0, 0, 0, 120));
                    g2d.fillRect(label.x - 2, label.y - fm.getAscent(), textWidth + 4, textHeight);

                    // Text
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(label.text, label.x, label.y);
                }
            }

            // Restore original transform
            g2d.setTransform(originalTransform);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NSTUPathfinder().setVisible(true);
            }
        });
    }
}