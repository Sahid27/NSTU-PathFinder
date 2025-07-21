import java.awt.Color;

public class Connection {
    private Node from, to;
    private String name;
    private double weight;
    private boolean isSafe;
    private boolean isUnderConstruction;
    private Color color;

    public Connection(Node from, Node to, String name, double weight) {
        this.from = from;
        this.to = to;
        this.name = name;
        this.weight = weight;
        this.isSafe = true;
        this.isUnderConstruction = false;
        this.color = Color.WHITE;
    }

    public Connection(Node from, Node to, String name, double weight, boolean isSafe) {
        this(from, to, name, weight);
        this.isSafe = isSafe;
    }

    // Getters and setters
    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public boolean isSafe() { return isSafe; }
    public boolean isUnderConstruction() { return isUnderConstruction; }
    public Color getColor() { return color; }

    public void setSafe(boolean safe) { this.isSafe = safe; }
    public void setUnderConstruction(boolean underConstruction) {
        this.isUnderConstruction = underConstruction;
        this.color = underConstruction ? Color.RED : Color.WHITE;
    }
    public void setColor(Color color) { this.color = color; }

    public String getConnectionKey() {
        return from.getId() + "-" + to.getId();
    }

    public String getReverseConnectionKey() {
        return to.getId() + "-" + from.getId();
    }

    @Override
    public String toString() {
        return from.getName() + " -> " + to.getName() + " (" + name + ")";
    }
}