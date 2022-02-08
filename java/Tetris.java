import java.awt.event.*;

class Tetris {
  public static void main(String[] args) {
    Game game = new Game();

    Thread t1 = new Thread(() -> game.run());

    Thread t2 = new Thread(() -> {
      while (!game.board.isOver()) {
        try {
          int level = game.board.getLevel();
          Thread.sleep(1000 - 50L * (level - 1));

          if(!game.board.movePiece(2)) {
            game.board.consolidate();
          }

          game.board.refresh();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    t1.start();
    t2.start();
  }
}
