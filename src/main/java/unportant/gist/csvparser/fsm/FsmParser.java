package unportant.gist.csvparser.fsm;

import unportant.gist.csvparser.Parser;

import java.util.ArrayList;
import java.util.List;

public class FsmParser implements Parser {

  enum State {
    READY,
    SIMPLE_FIELD,
    QUOTED_FIELD,
    END_OF_QUOTED_OR_ESCAPED_QUOTE
  }

  private final char fieldSeparator;
  private final char quote;
  private final int expectedFieldCount;

  // Theses two are reused to avoid useless garbage
  private final ReusableReader reader = new ReusableReader();
  private final StringBuilder currentField = new StringBuilder();

  private State state;
  private List<String> fields;

  public FsmParser(char fieldSeparator, char quote, int expectedFieldCount) {
    this.fieldSeparator = fieldSeparator;
    this.quote = quote;
    this.expectedFieldCount = expectedFieldCount;
  }

  public List<String> parse(String line) {
    reader.reset(line);
    currentField.setLength(0);

    fields =new ArrayList<>(expectedFieldCount);
    state = State.READY;

    while (!reader.isExhausted()) {
      switch (state) {
        case READY:
          ready();
          break;
        case SIMPLE_FIELD:
          simpleField();
          break;
        case QUOTED_FIELD:
          quotedField();
          break;
        case END_OF_QUOTED_OR_ESCAPED_QUOTE:
          endOfQuotedOrEscapedQuote();
          break;
      }
    }

    switch (state) {
      case READY:
      case SIMPLE_FIELD:
      case END_OF_QUOTED_OR_ESCAPED_QUOTE:
        addField();
        break;
      case QUOTED_FIELD:
        throw new IllegalStateException();
    }

    return fields;
  }

  private void ready() {
    char c = reader.read();
    if (isQuote(c)) {
      state = State.QUOTED_FIELD;
    } else if (isFieldSeparator(c)) {
      addField();
      state = State.READY;
    } else {
      append(c);
      state = State.SIMPLE_FIELD;
    }
  }

  private void simpleField() {
    char c = reader.read();
    if (isFieldSeparator(c)) {
      addField();
      state = State.READY;
    } else {
      append(c);
    }
  }

  private void quotedField() {
    char c = reader.read();
    if (isQuote(c)) {
      state = State.END_OF_QUOTED_OR_ESCAPED_QUOTE;
    } else {
      append(c);
    }
  }

  private void endOfQuotedOrEscapedQuote() {
    char c = reader.read();
    if (isQuote(c)) {
      append(quote);
      state = State.QUOTED_FIELD;
    } else if (isFieldSeparator(c)) {
      addField();
      state = State.READY;
    } else {
      throw new IllegalStateException();
    }
  }

  private void addField() {
    fields.add(currentField.toString());
    currentField.setLength(0);
  }

  private void append(char c) {
    currentField.append(c);
  }

  private boolean isQuote(char c) {
    return c == quote;
  }

  private boolean isFieldSeparator(char c) {
    return c == fieldSeparator;
  }
}
