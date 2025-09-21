// src/main/java/ch/so/agi/mcp/util/NameValidator.java
package ch.so.agi.mcp.util;

import java.util.regex.Pattern;

public final class NameValidator {
  // ASCII letters only
  private static final Pattern ASCII = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");
  // Unicode letters version (optional). Pick ONE and use consistently.
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
      throw new IllegalArgumentException(
          what + " must match " + (pattern == ASCII ? "[A-Za-z][A-Za-z0-9_]*" : "\\p{L}[\\p{L}\\p{Nd}_]*")
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
