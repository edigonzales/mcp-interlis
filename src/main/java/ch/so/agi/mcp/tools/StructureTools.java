package ch.so.agi.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StructureTools {

  @Tool(
      name = "createStructureSnippet",
      description = "Erzeugt eine STRUCTURE-Definition (keine OID/TID). Params: name (required), isAbstract?, extendsFqn?, attrLines?"
  )
  public Map<String, Object> createStructure(
      @ToolParam(description = "Strukturname", required = true) String name,
      @ToolParam(description = "Abstrakt?") @Nullable Boolean isAbstract,
      @ToolParam(description = "EXTENDS (vollqualifiziert)") @Nullable String extendsFqn,
      @ToolParam(description = "Attribut-Zeilen (roher ILI-Text)") @Nullable List<String> attrLines
  ) {
    boolean abs = isAbstract != null && isAbstract;
    String header = "STRUCTURE " + name
        + (abs ? " (ABSTRACT)" : "")
        + (extendsFqn != null && !extendsFqn.isBlank() ? " EXTENDS " + extendsFqn.trim() : "")
        + " =";

    StringBuilder sb = new StringBuilder();
    sb.append(header).append("\n");

    if (attrLines != null && !attrLines.isEmpty()) {
      for (String l : attrLines) {
        sb.append("  ").append(l).append("\n");
      }
    } else {
      sb.append("  !! Attribute hier\n");
    }

    sb.append("END ").append(name).append(";");
    return Map.of("iliSnippet", sb.toString(), "cursorHint", Map.of("line", 1, "col", 2));
  }
}
