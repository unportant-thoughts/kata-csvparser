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

  // Instances reused across call to avoid allocations

  private final ReusableReader reader = new ReusableReader();
  private final StringBuilder currentField = new StringBuilder();
  private final List<String> fields = new ArrayList<String>();
  private State state;

  public FsmParser(char fieldSeparator, char quote) {
    this.fieldSeparator = fieldSeparator;
    this.quote = quote;
  }

  public List<String> parse(String line) {
    fields.clear();
    reader.reset(line);
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
        fields.add(currentField.toString());
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
      fields.add(currentField.toString());
      currentField.setLength(0);
      state = State.READY;
    } else {
      currentField.append(c);
      state = State.SIMPLE_FIELD;
    }
  }

  private void simpleField() {
    char c = reader.read();
    if (isFieldSeparator(c)) {
      fields.add(currentField.toString());
      currentField.setLength(0);
      state = State.READY;
    } else {
      currentField.append(c);
    }
  }

  private void quotedField() {
    char c = reader.read();
    if (isQuote(c)) {
      state = State.END_OF_QUOTED_OR_ESCAPED_QUOTE;
    } else {
      currentField.append(c);
    }
  }

  private void endOfQuotedOrEscapedQuote() {
    char c = reader.read();
    if (isQuote(c)) {
      currentField.append(quote);
      state = State.QUOTED_FIELD;
    } else if (isFieldSeparator(c)) {
      fields.add(currentField.toString());
      currentField.setLength(0);
      state = State.READY;
    } else {
      throw new IllegalStateException();
    }
  }


  private boolean isQuote(char c) {
    return c == quote;
  }

  private boolean isFieldSeparator(char c) {
    return c == fieldSeparator;
  }
}
