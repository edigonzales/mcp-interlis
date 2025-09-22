package ch.so.agi.mcp.tools;

import ch.so.agi.mcp.util.NameValidator;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IdentifierTools {

  @Tool(name = "sanitizeIdentifier",
        description = "Sanitize a string to a valid INTERLIS identifier: Letter { Letter | Digit | '_' }. Returns the sanitized value and whether it changed.")
  public Map<String,Object> sanitizeIdentifier(@ToolParam(description = "Free-form value", required = true) String value) {
    if (value == null) value = "";
    String trimmed = value.trim();

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
        sb.append(c);
      } else {
        sb.append('_');
      }
    }
    String s = sb.toString();
    if (s.isEmpty() || !Character.isLetter(s.charAt(0))) {
      s = "X_" + s;
    }
    s = s.replaceAll("_+", "_");
    s = s.replaceAll("_+$", "");

    boolean changed = !s.equals(trimmed);
    return Map.of("value", s, "changed", changed);
  }

  @Tool(name = "validateIdentifier",
        description = "Validate an INTERLIS identifier against the rule: ^[A-Za-z][A-Za-z0-9_]*$. Returns {valid:true} or throws an error.")
  public Map<String,Object> validateIdentifier(@ToolParam(description = "Identifier to validate", required = true) String value) {
    var nv = NameValidator.ascii();
    nv.validateIdent(value, "Identifier");
    return Map.of("valid", true);
  }

  @Tool(name = "validateFqn",
        description = "Validate a fully qualified name (dot-separated identifiers). Returns {valid:true} or throws an error.")
  public Map<String,Object> validateFqn(@ToolParam(description = "FQN to validate", required = true) String fqn) {
    var nv = NameValidator.ascii();
    nv.validateFqn(fqn, "FQN");
    return Map.of("valid", true);
  }
}
