package unportant.gist.csvparser.fsm;

final class ReusableReader {

  private String line;
  private int pos;

  void reset(String line) {
    this.line = line;
    this.pos = 0;
  }

  char read() {
    char c = line.charAt(pos);
    pos += 1;
    return c;
  }

  boolean isExhausted() {
    return pos == line.length();
  }
}
