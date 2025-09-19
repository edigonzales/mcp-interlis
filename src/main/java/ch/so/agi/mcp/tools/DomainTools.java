package ch.so.agi.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DomainTools {

  @Tool(name = "createEnumDomainSnippet",
        description = "Erzeugt eine Aufzählungs-DOMAIN. Params: name (required), items (required: list of enum items).")
  public Map<String,Object> createEnumDomain(
      @ToolParam(description = "Domain-Name", required = true) String name,
      @ToolParam(description = "Enum-Items in Reihenfolge", required = true) List<String> items
  ) {
    String inner = items.stream().map(String::trim).collect(Collectors.joining(", "));
    String snippet = "DOMAIN\n  " + name + " = (" + inner + ");";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createNumericDomainSnippet",
        description = "Erzeugt eine numerische DOMAIN. Params: name (required), min, max (required), unitFQN (optional).")
  public Map<String,Object> createNumericDomain(
      @ToolParam(description = "Domain-Name", required = true) String name,
      @ToolParam(description = "Minimum", required = true) String min,
      @ToolParam(description = "Maximum", required = true) String max,
      @ToolParam(description = "Einheits-FQN, z. B. 'INTERLIS.m'") @Nullable String unitFqn
  ) {
    String range = min.trim() + " .. " + max.trim();
    String unit = (unitFqn != null && !unitFqn.isBlank()) ? " [" + unitFqn.trim() + "]" : "";
    String snippet = "DOMAIN\n  " + name + " = " + range + unit + ";";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }

  @Tool(name = "createUnitSnippet",
        description = "Erzeugt eine UNIT-Definition. Params: name (required), kind (e.g. LENGTH), base (e.g. INTERLIS.m).")
  public Map<String,Object> createUnit(
      @ToolParam(description = "Einheiten-Name", required = true) String name,
      @ToolParam(description = "Einheitsart, z. B. LENGTH, AREA", required = true) String kind,
      @ToolParam(description = "Basis-Einheit, z. B. INTERLIS.m", required = true) String base
  ) {
    String snippet = "UNIT\n  " + name + " = " + kind.trim() + " [" + base.trim() + "];";
    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }
}
