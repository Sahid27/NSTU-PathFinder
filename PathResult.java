import java.util.List;
import java.util.ArrayList;

public class PathResult {
    private List<Node> path;
    private double totalWeight;
    private List<String> connectionNames;

    public PathResult(List<Node> path, double totalWeight) {
        this.path = new ArrayList<>(path);
        this.totalWeight = totalWeight;
        this.connectionNames = new ArrayList<>();
    }

    public PathResult(List<Node> path, double totalWeight, List<String> connectionNames) {
        this.path = new ArrayList<>(path);
        this.totalWeight = totalWeight;
        this.connectionNames = new ArrayList<>(connectionNames);
    }

    // Getters
    public List<Node> getPath() { return new ArrayList<>(path); }
    public double getTotalWeight() { return totalWeight; }
    public List<String> getConnectionNames() { return new ArrayList<>(connectionNames); }

    public int getSegmentCount() {
        return path.size() > 0 ? path.size() - 1 : 0;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append(" (Weight: ").append(String.format("%.2f", totalWeight)).append(")");
        return sb.toString();
    }
}