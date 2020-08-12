import java.util.Objects;

public class LumberType implements Comparable<LumberType>{
    private int height;
    private int width;

    public LumberType(int height, int width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LumberType that = (LumberType) o;
        return height == that.height &&
                width == that.width;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }

    @Override
    public String toString() {
        return height+"x"+width;
    }

    @Override
    public int compareTo(LumberType o) {
        int h = height-o.height;
        return h == 0 ? width-o.width : h;
    }
}
