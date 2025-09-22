package ch.so.agi.mcp.util;

import java.util.regex.Pattern;

public final class NameValidator {
  private static final Pattern ASCII = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");
  private static final Pattern UNICODE = Pattern.compile("^\\p{L}[\\p{L}\\p{Nd}_]*$");

  private final Pattern pattern;

  private NameValidator(Pattern pattern) { this.pattern = pattern; }

  public static NameValidator ascii()   { return new NameValidator(ASCII); }
  public static NameValidator unicode() { return new NameValidator(UNICODE); }

  public void validateIdent(String value, String what) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(what + " is required.");
    }
    if (!pattern.matcher(value).matches()) {
      String rule = (pattern == ASCII) ? "[A-Za-z][A-Za-z0-9_]*" : "\\p{L}[\\p{L}\\p{Nd}_]*";
      throw new IllegalArgumentException(what + " must match " + rule
          + " (starts with a letter, then letters/digits/underscore). Got: '" + value + "'.");
    }
  }

  public void validateFqn(String fqn, String what) {
    if (fqn == null || fqn.isBlank()) {
      throw new IllegalArgumentException(what + " is required.");
    }
    String[] parts = fqn.split("\\.");
    for (String p : parts) {
      validateIdent(p, what + " segment");
    }
  }
}
