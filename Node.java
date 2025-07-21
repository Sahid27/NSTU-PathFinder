import java.util.Objects;

public class Node {
    private int x, y;
    private String id;
    private String name;
    private boolean isSafe;

    public Node(int x, int y, String id, String name) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.name = name;
        this.isSafe = true; // Default to safe
    }

    public Node(int x, int y, String id, String name, boolean isSafe) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.name = name;
        this.isSafe = isSafe;
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isSafe() { return isSafe; }
    public void setSafe(boolean safe) { this.isSafe = safe; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y && Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, id);
    }
}