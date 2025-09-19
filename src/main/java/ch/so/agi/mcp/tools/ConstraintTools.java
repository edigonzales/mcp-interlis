package ch.so.agi.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConstraintTools {

  @Tool(name = "createUniqueConstraint",
        description = "UNIQUE-Constraint: Params: attrs (required list). Returns a CONSTRAINTS block (append inside class).")
  public Map<String,Object> unique(
      @ToolParam(description = "Attribute (z. B. ['bezeich','lage'])", required = true) List<String> attrs
  ) {
    String inner = attrs.stream().map(String::trim).collect(Collectors.joining(", "));
    String snippet = "CONSTRAINTS\n  UNIQUE (" + inner + ");";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createMandatoryConstraint",
        description = "MANDATORY CONSTRAINT: Params: expr (required).")
  public Map<String,Object> mandatory(
      @ToolParam(description = "boolescher Ausdruck", required = true) String expr
  ) {
    String snippet = "CONSTRAINTS\n  MANDATORY CONSTRAINT " + expr.trim() + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createSetConstraint",
        description = "SET CONSTRAINT: Params: expr (required).")
  public Map<String,Object> setConstraint(
      @ToolParam(description = "Mengen-Ausdruck", required = true) String expr
  ) {
    String snippet = "CONSTRAINTS\n  SET CONSTRAINT\n    " + expr.trim() + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 2, "col", 4));
  }

  @Tool(name = "createPresentIfConstraint",
        description = "PRESENT ... IF ...: Params: attr (required), cond (required).")
  public Map<String,Object> presentIf(
      @ToolParam(description = "Attribut", required = true) String attr,
      @ToolParam(description = "Bedingung", required = true) String cond
  ) {
    String snippet = "CONSTRAINTS\n  PRESENT " + attr.trim() + " IF " + cond.trim() + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createValueRangeConstraint",
        description = "VALUE ... IN ...: Params: attr (required), range (required).")
  public Map<String,Object> valueIn(
      @ToolParam(description = "Attribut", required = true) String attr,
      @ToolParam(description = "Range, z. B. '0.0 .. 4000.0'", required = true) String range
  ) {
    String snippet = "CONSTRAINTS\n  VALUE " + attr.trim() + " IN " + range.trim() + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createExistenceConstraint",
        description = "EXISTENCE CONSTRAINT ... REQUIRED IN ... : Params: refAttr (required), classFQNs (required list).")
  public Map<String,Object> existence(
      @ToolParam(description = "Referenzattribut", required = true) String refAttr,
      @ToolParam(description = "Erlaubte Klassen (FQNs)", required = true) List<String> classFqns
  ) {
    String inner = classFqns.stream().map(String::trim).collect(Collectors.joining(", "));
    String snippet = "CONSTRAINTS\n  EXISTENCE CONSTRAINT " + refAttr.trim() + " REQUIRED IN " + inner + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }
}
