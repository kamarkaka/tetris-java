import java.awt.*;

/*
 * Board extends an AWT window object that displays the tetris game board.
 */
class Board extends Frame {
  static int WIDTH = 10;
  static int HEIGHT = 10;
  static int WINDOW_WIDTH = 200;
  static int WINDOW_HEIGHT = 1000;
  static int TOP_BORDER = 30;
  static int BORDER = 10;

  TextArea area;

  // The board is represented as an array of arrays, with 10 rows and 10 columns.
  private int[][] board = new int[HEIGHT][WIDTH];
  private Piece currPiece = null;
  private Piece nextPiece = null;
  private Piece ghostPiece = null;
  private Piece stashedPiece = null;
  private boolean isOver = false;
  private boolean stashed = false;
  private int score = 0;
  private int level = 0;

  Board() {
    super("Demo");
    setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
    setLayout(null);
    setVisible(true);
    requestFocus();

    area = new TextArea("", WIDTH, HEIGHT, TextArea.SCROLLBARS_NONE);
    area.setBounds(
        BORDER,
        TOP_BORDER,
        WINDOW_WIDTH - 2 * BORDER,
        WINDOW_HEIGHT - BORDER - TOP_BORDER);
    area.setFont(new Font("Monospaced", Font.PLAIN, 24));
    area.setEditable(false);
    area.setFocusable(false);

    add(area);
  }

  public int getLevel() {
    return level;
  }
  public boolean isOver() {
    return isOver;
  }

  public void addPiece() {
    if (currPiece == null && nextPiece == null) {
      currPiece = new Piece();
      nextPiece = new Piece();
    } else if (currPiece == null) {
      currPiece = nextPiece;
      nextPiece = new Piece();
      stashed = true;
    }

    updateGhost();
  }

  /*
   *  1 -> right
   * -1 -> left
   *  2 -> down
   * -2 -> up
   * return true if moved, otherwise false
   */
  public boolean movePiece(int direction) {
    boolean moved = movePiece(currPiece, direction);
    if (moved && Math.abs(direction) != 2) {
      updateGhost();
    }
    return moved;
  }

  public void rotatePiece() {
    int[][] originalPoints = Utility.copy(currPiece.points);

    int offsetX = currPiece.position[1];
    int leftmost = Integer.MAX_VALUE, rightmost = Integer.MIN_VALUE;

    for (int i = 0; i < currPiece.points.length; i++) {
      int x = offsetX + currPiece.points[i][1];
      if (x < leftmost) leftmost = x;
      if (x > rightmost) rightmost = x;
    }

    if (leftmost == 0) {
      // stick to left
      currPiece.rotate(-1);
    } else if (rightmost == WIDTH - 1) {
      // stick to right
      currPiece.rotate(WIDTH);
    } else {
      currPiece.rotate(0);
    }

    if (!detectCollision(currPiece)) {
      updateGhost();
      return;
    }

    currPiece.points = originalPoints;
  }

  public void dropPiece() {
    currPiece = ghostPiece;
  }

  // move current Piece to stash if it's empty
  // swap if not
  // once stashed or swapped, user cannot repeat until a new currentPiece is placed
  public void stashPiece() {
    if (stashedPiece == null) {
      stashedPiece = new Piece(currPiece.Type);
      currPiece = nextPiece;
      nextPiece = new Piece();
    } else if (stashed) {
      Piece tmp = stashedPiece;
      stashedPiece = new Piece(currPiece.Type);
      currPiece = tmp;
      stashed = false;
    }
  }

  // mark current Piece into board
  public void consolidate() {
    int offsetX = currPiece.position[1], offsetY = currPiece.position[0];
    for (int[] point : currPiece.points) {
      int x = point[1] + offsetX;
      int y = point[0] + offsetY;
      if (y >= 0) board[y][x] = 1;
      else isOver = true;
    }

    if (!isOver) {
      currPiece = null;
      clearRows();
      addPiece();
    }
  }

  // Append text to the demo text area.
  public void append(String text) {
    area.append(text);
  }

  // Update the demo text area with the contents of the board.
  public void refresh() {
    StringBuilder sb = new StringBuilder();

    // score
    sb.append("Score: ").append(score);
    sb.append("\n");

    // level
    sb.append("Level: ").append(level);
    sb.append("\n");

    sb.append("*".repeat(Math.max(0, WIDTH + 2)));
    sb.append("\n");

    // stash preview
    for (int row = 0; row < 2; row++) {
      sb.append("|");
      for (int col = 0; col < WIDTH; col++) {
        int val = 0;
        if (stashedPiece != null) {
          val = update(stashedPiece, 0, 2, row, col, 1, 0);
        }

        sb.append(val == 1 ? '#' : ' ');
      }
      sb.append("|\n");
    }

    sb.append("=".repeat(Math.max(0, WIDTH + 2)));
    sb.append("\n");

    // next piece preview
    for (int row = 0; row < 2; row++) {
      sb.append("|");
      for (int col = 0; col < WIDTH; col++) {
        int val = update(nextPiece, 0, 2, row, col, 1, 0);
        sb.append(val == 1 ? '#' : ' ');
      }
      sb.append("|\n");
    }

    sb.append("=".repeat(Math.max(0, WIDTH + 2)));
    sb.append("\n");

    // draw ghost
    for (int row = 0; row < HEIGHT; row++) {
      sb.append("|");
      for (int col = 0; col < WIDTH; col++) {
        int val1 = update(ghostPiece, 0, 0, row, col, 2, board[row][col]);
        int val2 = update(currPiece, 0, 0, row, col, 1, board[row][col]);
        sb.append((val1 == 1 || val2 == 1) ? '#' : (val1 == 2 ? '.' : ' '));
      }
      sb.append("|\n");
    }

    sb.append("*".repeat(Math.max(0, WIDTH + 2)));
    sb.append("\n");

    // game over
    if (isOver) {
      sb.append("Game Over!");
      sb.append("\n");
    }

    area.setText(sb.toString());
  }

  public void restart() {
    board = new int[HEIGHT][WIDTH];
    currPiece = null;
    nextPiece = null;
    ghostPiece = null;
    stashedPiece = null;
    isOver = false;
    stashed = false;
    score = 0;
    level = 0;

    addPiece();
  }

  private boolean movePiece(Piece piece, int direction) {
    piece.move(direction);
    if (!detectCollision(piece)) return true;

    piece.move(-direction);
    return false;
  }

  private void updateGhost() {
    ghostPiece = new Piece(currPiece.Type);
    ghostPiece.position = Utility.copy(currPiece.position);
    ghostPiece.points = Utility.copy(currPiece.points);

    while (movePiece(ghostPiece, 2)) {}
  }

  // return true if there is a collision, otherwise false
  private boolean detectCollision(Piece piece) {
    int offsetY = piece.position[0], offsetX = piece.position[1];
    for (int[] point : piece.points) {
      int y = point[0] + offsetY, x = point[1] + offsetX;
      if (x < 0 || x >= WIDTH) return true;
      if (y >= HEIGHT) return true;
      if (y < 0) continue; // piece can be place above board
      if (board[y][x] != 0) return true;
    }
    return false;
  }

  // clear a line if it's filled
  private void clearRows() {
    int[][] newBoard = new int[HEIGHT][WIDTH];
    int p = HEIGHT - 1;
    for (int row = HEIGHT - 1; row >= 0; row--) {
      boolean clearRow = true;
      for (int col = 0; col < WIDTH; col++) {
        if (board[row][col] == 0) {
          clearRow = false;
          break;
        }
      }

      if (clearRow) {
        score += 10;
        level = score / 100 + 1;
        level = Math.min(level, 10);
      } else {
        newBoard[p] = Utility.copy(board[row]);
        p--;
      }
    }

    board = newBoard;
  }

  // Update Piece to a value on board
  private int update(Piece piece, int dx, int dy, int row, int col, int val, int defaultVal) {
    int offsetX = piece.position[1] + dx;
    int offsetY = piece.position[0] + dy;
    for (int[] point : piece.points) {
      if (point[1] + offsetX == col && point[0] + offsetY == row) {
        return val;
      }
    }
    return defaultVal;
  }
}
