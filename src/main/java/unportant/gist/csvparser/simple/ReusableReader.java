package unportant.gist.csvparser.simple;

final class ReusableReader {

  private String line;
  private int nextPos;

  private char lastChar;
  private char currentChar;

  void reset(String line) {
    this.line = line;
    this.nextPos = 0;
  }

  char lastChar() {
    return lastChar;
  }

  char peek(){
    return line.charAt(nextPos);
  }

  char current() {
    return currentChar;
  }

  char read() {
    lastChar = currentChar;
    currentChar = line.charAt(nextPos);
    nextPos += 1;
    return currentChar;
  }

  boolean isExhausted() {
    return nextPos == line.length();
  }
}
