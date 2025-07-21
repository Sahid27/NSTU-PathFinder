public class MapLabel {
    private String text;
    private int x, y;

    public MapLabel(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    // Getters
    public String getText() { return text; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Setters
    public void setText(String text) { this.text = text; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}