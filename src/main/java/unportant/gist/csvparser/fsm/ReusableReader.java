package unportant.gist.csvparser.fsm;

final class ReusableReader {

  private String line;
  private int pos;

  private char lastChar;

  void reset(String line) {
    this.line = line;
    this.pos = 0;
  }

  char lastChar() {
    return lastChar;
  }

  char peek(){
    return line.charAt(pos);
  }

  char read() {
    lastChar = line.charAt(pos);
    pos += 1;
    return lastChar;
  }

  boolean isExhausted() {
    return pos == line.length();
  }
}
