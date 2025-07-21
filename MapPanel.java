import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MapPanel extends JPanel {
    private List<Node> nodes;
    private List<Connection> connections;
    private List<MapLabel> labels;
    private List<PathResult> allPaths;
    private Node selectedNode;
    private NodeClickListener nodeClickListener;
    private CoordinateClickListener coordinateClickListener;

    // Colors
    private final Color backgroundColor = new Color(102, 175, 38);
    private final Color nodeColor = new Color(12, 12, 12);
    private final Color pathColor = new Color(255, 255, 255);
    private final Color shortestPathColor = new Color(210, 30, 30);
    private final Color secondPathColor = new Color(255, 165, 0);
    private final Color thirdPathColor = new Color(255, 255, 0);
    private final Color highlightColor = new Color(210, 30, 30);
    private final Color labelBackgroundColor = new Color(165, 171, 44);
    private final Color textColor = new Color(255, 255, 255);
    private final Color constructionColor = new Color(255, 0, 0);
    private final Color unsafeColor = new Color(255, 100, 100);

    public interface NodeClickListener {
        void onNodeClicked(Node node);
    }

    public interface CoordinateClickListener {
        void onCoordinateClicked(int x, int y);
    }

    public MapPanel(List<Node> nodes, List<Connection> connections, List<MapLabel> labels) {
        this.nodes = nodes;
        this.connections = connections;
        this.labels = labels;
        this.allPaths = new java.util.ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    public void setNodeClickListener(NodeClickListener listener) {
        this.nodeClickListener = listener;
    }

    public void setCoordinateClickListener(CoordinateClickListener listener) {
        this.coordinateClickListener = listener;
    }

    public void setPaths(List<PathResult> paths) {
        this.allPaths = paths;
        repaint();
    }

    public void setSelectedNode(Node node) {
        this.selectedNode = node;
        repaint();
    }

    private void handleMouseClick(int x, int y) {
        boolean nodeClicked = false;

        // Check if a node was clicked
        for (Node node : nodes) {
            double distance = Math.sqrt(Math.pow(node.getX() - x, 2) + Math.pow(node.getY() - y, 2));
            if (distance <= 10) {
                nodeClicked = true;
                selectedNode = node;
                if (nodeClickListener != null) {
                    nodeClickListener.onNodeClicked(node);
                }
                repaint();
                break;
            }
        }

        if (!nodeClicked) {
            selectedNode = null;
            if (coordinateClickListener != null) {
                coordinateClickListener.onCoordinateClicked(x, y);
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all connections first
        drawConnections(g2d);

        // Draw paths
        drawPaths(g2d);

        // Draw nodes
        drawNodes(g2d);

        // Draw labels
        drawLabels(g2d);
    }

    private void drawConnections(Graphics2D g2d) {
        for (Connection conn : connections) {
            Color connectionColor = pathColor;
            int strokeWidth = 1;

            if (conn.isUnderConstruction()) {
                connectionColor = constructionColor;
                strokeWidth = 3;
            } else if (!conn.isSafe()) {
                connectionColor = unsafeColor;
                strokeWidth = 2;
            }

            g2d.setColor(connectionColor);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.drawLine(conn.getFrom().getX(), conn.getFrom().getY(),
                    conn.getTo().getX(), conn.getTo().getY());
        }
    }

    private void drawPaths(Graphics2D g2d) {
        // Draw multiple shortest paths with different colors and thickness
        for (int pathIndex = allPaths.size() - 1; pathIndex >= 0; pathIndex--) {
            PathResult pathResult = allPaths.get(pathIndex);
            List<Node> path = pathResult.getPath();

            if (!path.isEmpty()) {
                Color pathColor;
                int strokeWidth;

                if (pathIndex == 0) {
                    pathColor = shortestPathColor;
                    strokeWidth = 6;
                } else if (pathIndex == 1) {
                    pathColor = secondPathColor;
                    strokeWidth = 4;
                } else {
                    pathColor = thirdPathColor;
                    strokeWidth = 3;
                }

                g2d.setStroke(new BasicStroke(strokeWidth));
                g2d.setColor(pathColor);

                for (int i = 0; i < path.size() - 1; i++) {
                    Node current = path.get(i);
                    Node next = path.get(i + 1);
                    g2d.drawLine(current.getX(), current.getY(), next.getX(), next.getY());
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2d) {
        for (Node node : nodes) {
            // Check if node is part of any path
            boolean isInPath = false;
            for (PathResult pathResult : allPaths) {
                if (pathResult.getPath().contains(node)) {
                    isInPath = true;
                    break;
                }
            }

            // Determine node color
            Color nodeDrawColor = nodeColor;
            if (isInPath) {
                nodeDrawColor = highlightColor;
            }

            g2d.setColor(nodeDrawColor);
            g2d.fillOval(node.getX() - 6, node.getY() - 6, 12, 12);

            // Draw highlight around selected node
            if (node == selectedNode) {
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(node.getX() - 9, node.getY() - 9, 18, 18);
            }

            // Draw node safety indicator
            if (!node.isSafe()) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(node.getX() - 8, node.getY() - 8, 16, 16);
            }
        }
    }

    private void drawLabels(Graphics2D g2d) {
        for (MapLabel label : labels) {
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(label.getText());
            int textHeight = fm.getHeight();

            // Background
            g2d.setColor(labelBackgroundColor);
            g2d.fillRect(label.getX() - 2, label.getY() - fm.getAscent(), textWidth + 4, textHeight);

            // Text
            g2d.setColor(textColor);
            g2d.drawString(label.getText(), label.getX(), label.getY());
        }
    }
}