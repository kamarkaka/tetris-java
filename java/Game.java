import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Game implements KeyListener {
   public Board board = new Board();
   private final int[] keyCodes = new int[7];

   public Game() {
      this(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE, KeyEvent.VK_C, KeyEvent.VK_R);
   }

   public Game(int up, int down, int left, int right, int drop, int stash, int restart) {
      keyCodes[0] = up;
      keyCodes[1] = down;
      keyCodes[2] = left;
      keyCodes[3] = right;
      keyCodes[4] = drop;
      keyCodes[5] = stash;
      keyCodes[6] = restart;
   }

   public void run() {
      board.addPiece();
      board.refresh();

      board.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            System.exit(0);
         }
      });
      board.addKeyListener(this);
   }

   public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();

      if (e.isControlDown() && keyCode == KeyEvent.VK_C) {
         System.exit(0);
      }

      // only accepts restart game
      if (keyCode == keyCodes[6]) {
         board.append("RESTART");
         board.restart();
         return;
      }

      if (!board.isOver()) {
         if (keyCode == keyCodes[0]) {
            board.append("UP");
            board.rotatePiece();
         } else if (keyCode == keyCodes[1]) {
            board.append("DOWN");
            board.movePiece(2);
         } else if (keyCode == keyCodes[2]) {
            board.append("LEFT");
            board.movePiece(-1);
         } else if (keyCode == keyCodes[3]) {
            board.append("RIGHT");
            board.movePiece(1);
         } else if (keyCode == keyCodes[4]) {
            board.append("DROP");
            board.dropPiece();
         } else if (keyCode == keyCodes[5]) {
            board.append("STASH");
            board.stashPiece();
         } else {
            board.append(KeyEvent.getKeyText(keyCode));
         }
      }
   }

   public void keyReleased(KeyEvent e) {
      board.refresh();
   }

   @Override
   public void keyTyped(KeyEvent e) {}
}
