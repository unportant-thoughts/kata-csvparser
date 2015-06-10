package unportant.gist.csvparser.simple;

class ReusableToken {

  enum Type {
    FIELD,
    LAST_FIELD,
    EOF,
  }

  private Type type;
  private StringBuilder content = new StringBuilder();

  public Type getType() {
    return type;
  }

  public String getContent() {
    return content.toString();
  }

  public void appendContent(char c) {
    content.append(c);
  }

  void reset(Type type) {
    this.content.setLength(0);
    this.type = type;
  }

  @Override
  public String toString() {
    return "Token{" +
            "type=" + type +
            ", content=" + content +
            '}';
  }
}
