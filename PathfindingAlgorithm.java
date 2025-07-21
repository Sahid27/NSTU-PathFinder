import java.util.*;

public class PathfindingAlgorithm {

    public static class NodeDistance implements Comparable<NodeDistance> {
        Node node;
        double distance;

        NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static List<PathResult> findMultipleShortestPaths(List<Node> nodes, List<Connection> connections,
                                                             Node startNode, Node endNode, boolean secureMode, int k) {
        List<PathResult> results = new ArrayList<>();

        if (startNode == null || endNode == null) {
            return results;
        }

        // Build graph
        Map<Node, List<Node>> graph = new HashMap<>();
        Map<String, Double> weights = new HashMap<>();
        Map<String, Connection> connectionMap = new HashMap<>();

        for (Connection conn : connections) {
            // Skip paths under construction
            if (conn.isUnderConstruction()) continue;

            // Skip unsafe paths in secure mode
            if (secureMode && !conn.isSafe()) continue;

            graph.putIfAbsent(conn.getFrom(), new ArrayList<>());
            graph.get(conn.getFrom()).add(conn.getTo());
            graph.putIfAbsent(conn.getTo(), new ArrayList<>());
            graph.get(conn.getTo()).add(conn.getFrom());

            String edgeKey = conn.getConnectionKey();
            String reverseEdgeKey = conn.getReverseConnectionKey();
            weights.put(edgeKey, conn.getWeight());
            weights.put(reverseEdgeKey, conn.getWeight());
            connectionMap.put(edgeKey, conn);
            connectionMap.put(reverseEdgeKey, conn);
        }

        // Find k shortest paths using modified Dijkstra
        results = findKShortestPaths(graph, weights, connectionMap, startNode, endNode, k);
        return results;
    }

    private static List<PathResult> findKShortestPaths(Map<Node, List<Node>> graph, Map<String, Double> weights,
                                                       Map<String, Connection> connectionMap, Node start, Node end, int k) {
        List<PathResult> paths = new ArrayList<>();

        // Find first shortest path
        PathResult shortestPath = dijkstraPath(graph, weights, connectionMap, start, end, new HashSet<>());
        if (shortestPath != null && !shortestPath.isEmpty()) {
            paths.add(shortestPath);
        }

        // Find alternative paths by temporarily removing edges
        Set<String> removedEdges = new HashSet<>();
        for (int pathCount = 1; pathCount < k && !paths.isEmpty(); pathCount++) {
            PathResult bestAlternative = null;
            double bestWeight = Double.MAX_VALUE;
            String bestRemovedEdge = null;

            PathResult lastPath = paths.get(paths.size() - 1);
            List<Node> lastPathNodes = lastPath.getPath();

            for (int i = 0; i < lastPathNodes.size() - 1; i++) {
                Node from = lastPathNodes.get(i);
                Node to = lastPathNodes.get(i + 1);
                String edgeKey = from.getId() + "-" + to.getId();
                String reverseEdgeKey = to.getId() + "-" + from.getId();

                if (!removedEdges.contains(edgeKey)) {
                    Set<String> tempRemovedEdges = new HashSet<>(removedEdges);
                    tempRemovedEdges.add(edgeKey);
                    tempRemovedEdges.add(reverseEdgeKey);

                    PathResult alternative = dijkstraPath(graph, weights, connectionMap, start, end, tempRemovedEdges);
                    if (alternative != null && !alternative.isEmpty() &&
                            alternative.getTotalWeight() < bestWeight &&
                            !isPathDuplicate(alternative, paths)) {
                        bestAlternative = alternative;
                        bestWeight = alternative.getTotalWeight();
                        bestRemovedEdge = edgeKey;
                    }
                }
            }

            if (bestAlternative != null) {
                paths.add(bestAlternative);
                if (bestRemovedEdge != null) {
                    removedEdges.add(bestRemovedEdge);
                    removedEdges.add(bestRemovedEdge.split("-")[1] + "-" + bestRemovedEdge.split("-")[0]);
                }
            } else {
                break;
            }
        }

        return paths;
    }

    private static boolean isPathDuplicate(PathResult newPath, List<PathResult> existingPaths) {
        for (PathResult existing : existingPaths) {
            if (existing.getPath().equals(newPath.getPath())) {
                return true;
            }
        }
        return false;
    }

    private static PathResult dijkstraPath(Map<Node, List<Node>> graph, Map<String, Double> weights,
                                           Map<String, Connection> connectionMap, Node start, Node end,
                                           Set<String> removedEdges) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();

        // Initialize distances
        for (Node node : graph.keySet()) {
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

                    String edgeKey = currentNode.getId() + "-" + neighbor.getId();
                    if (removedEdges.contains(edgeKey)) {
                        continue;
                    }

                    double weight = weights.getOrDefault(edgeKey, 1.0);
                    double totalDistance = distances.get(currentNode) + weight;

                    if (totalDistance < distances.get(neighbor)) {
                        distances.put(neighbor, totalDistance);
                        previousNodes.put(neighbor, currentNode);
                        queue.add(new NodeDistance(neighbor, totalDistance));
                    }
                }
            }
        }

        // Reconstruct path
        List<Node> path = new ArrayList<>();
        List<String> connectionNames = new ArrayList<>();
        Node current = end;

        if (!previousNodes.containsKey(end) && !start.equals(end)) {
            return null;
        }

        double totalWeight = distances.get(end);

        while (current != null) {
            path.add(current);
            Node previous = previousNodes.get(current);
            if (previous != null) {
                String edgeKey = previous.getId() + "-" + current.getId();
                Connection conn = connectionMap.get(edgeKey);
                if (conn != null) {
                    connectionNames.add(0, conn.getName());
                }
            }
            current = previous;
        }

        Collections.reverse(path);
        return new PathResult(path, totalWeight, connectionNames);
    }
}