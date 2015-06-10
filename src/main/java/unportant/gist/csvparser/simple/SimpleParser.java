package unportant.gist.csvparser.simple;

import unportant.gist.csvparser.Parser;
import unportant.gist.csvparser.simple.ReusableToken.Type;

import java.util.ArrayList;
import java.util.List;

public class SimpleParser implements Parser {

  private final char fieldSeparator;
  private final char quote;
  private final int expectedFieldCount;

  private ReusableToken token = new ReusableToken();
  private ReusableReader reader = new ReusableReader();

  public SimpleParser(char fieldSeparator, char quote, int expectedFieldCount) {
    this.fieldSeparator = fieldSeparator;
    this.quote = quote;
    this.expectedFieldCount = expectedFieldCount;
  }


  public List<String> parse(String line) {
    reader.reset(line);

    List<String> fields = new ArrayList<String>(expectedFieldCount);

    while (true) {
      token = readToken();
      switch (token.getType()) {
        case FIELD:
          fields.add(token.getContent());
          break;
        case LAST_FIELD:
          fields.add(token.getContent());
          return fields;
        case EOF:
          return fields;
        default:
          throw new IllegalStateException(token.toString());
      }
    }
  }

  private ReusableToken readToken() {
    if (reader.isExhausted()) {
      if (isFieldSeparator(reader.lastChar())) {
        token.reset(Type.LAST_FIELD);
      } else {
        token.reset(Type.EOF);
      }
      return token;
    }

    token.reset(Type.FIELD);

    char c = reader.peek();
    if (isQuote(c)) {
      return readQuotedToken();
    } else if (isFieldSeparator(c)) {
      return readEmptyToken();
    } else {
      return readSimpleToken();
    }
  }

  private ReusableToken readEmptyToken() {
    reader.read();
    return token;
  }

  private ReusableToken readSimpleToken() {
    while (!reader.isExhausted()) {
      char c = reader.read();
      if (isFieldSeparator(c)) {
        break;
      } else {
        token.appendContent(c);
      }
    }

    return token;
  }

  private ReusableToken readQuotedToken() {
    reader.read();

    while (!reader.isExhausted()) {
      char c = reader.read();
      if (isQuote(c)) {
        if (reader.isExhausted()) {
          break;
        }

        char n = reader.read();
        if (isFieldSeparator(n)) {
          break;
        } else if (isQuote(n)) {
          token.appendContent(quote);
        } else {
          throw new IllegalStateException();
        }
      } else {
        token.appendContent(c);
      }
    }

    if (reader.lastChar() != quote) {
      throw new IllegalStateException();
    }

    return token;
  }

  private boolean isQuote(char c) {
    return c == quote;
  }

  private boolean isFieldSeparator(char c) {
    return c == fieldSeparator;
  }

}
