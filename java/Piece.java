import java.util.Random;

public class Piece {
    private static final char[] SupportedTypes = {'i', 'o', 't', 's', 'z', 'j', 'l'};
    public char Type;
    public int[][] points = new int[4][2];
    public int[] position = new int[2];

    public Piece() {
        this(Piece.SupportedTypes[(new Random()).nextInt(Piece.SupportedTypes.length)]);
    }

    public Piece(char t) {
        position[0] = -2;
        position[1] = 3;
        Type = t;

        switch (Type) {
            case 'i' -> points = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}};
            case 'o' -> points = new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            case 't' -> points = new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, 2}};
            case 's' -> points = new int[][]{{0, 1}, {0, 2}, {1, 0}, {1, 1}};
            case 'z' -> points = new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 2}};
            case 'j' -> points = new int[][]{{0, 0}, {1, 0}, {1, 1}, {1, 2}};
            case 'l' -> points = new int[][]{{0, 2}, {1, 0}, {1, 1}, {1, 2}};
        }
    }

    //  1 -> right
    // -1 -> left
    //  2 -> down
    // -2 -> up
    public void move(int direction) {
        switch (direction) {
            case 1 -> position[1]++;
            case -1 -> position[1]--;
            case 2 -> position[0]++;
            case -2 -> position[0]--;
        }
    }

    // -1: stick to left
    // 1: stick to right
    // 0: normal rotation
    public void rotate(int step) {
        int[][] newPoint = points.clone();
        int[] anchor = new int[] {1,1};

        switch (Type) {
            case 'i':
                anchor = new int[] {0,1};
                break;
            case 'o':
                return;
        }

        // rotate
        for (int i = 0; i < points.length; i++) {
            int[] point = points[i];
            int dx = point[1] - anchor[1];
            int dy = point[0] - anchor[0];
            newPoint[i][0] = anchor[0] - dx;
            newPoint[i][1] = anchor[1] + dy;
        }

        if (step == 0) {
            points = newPoint;
            return;
        }

        int leftmost = Integer.MAX_VALUE, rightmost = Integer.MIN_VALUE;
        int offsetX = position[1];

        for (int[] point : points) {
            int x = offsetX + point[1];
            if (x < leftmost) leftmost = x;
            if (x > rightmost) rightmost = x;
        }

        if (step > 0) {
            // right
            for (int i = 0; i < points.length; i++) {
                points[i][1] += step - 1 - rightmost;
            }
        } else if (step < 0) {
            // left
            for (int i = 0; i < points.length; i++) {
                points[i][1] -= leftmost;
            }
        }

        points = newPoint;
    }
}
