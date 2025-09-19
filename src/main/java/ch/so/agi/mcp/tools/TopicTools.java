package ch.so.agi.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TopicTools {

  @Tool(name = "createTopicSnippet",
        description = "Erzeugt einen TOPIC-Block. Params: name (required), oidType (e.g. 'OID AS UUIDOID'), isAbstract (default false).")
  public Map<String,Object> createTopic(
      @ToolParam(description = "Topic-Name", required = true) String name,
      @ToolParam(description = "OID-Definition, z. B. 'OID AS UUIDOID'") @Nullable String oidType,
      @ToolParam(description = "Abstrakter Topic?") @Nullable Boolean isAbstract
  ) {
    boolean abs = isAbstract != null && isAbstract;
    String header = abs ? String.format("TOPIC %s (ABSTRACT) =", name) : String.format("TOPIC %s =", name);
    String oid = (oidType != null && !oidType.isBlank()) ? "  " + oidType.trim() + ";\n" : "";
    String snippet = header + "\n" + oid + "  !! Klassen/Assoziationen hier\nEND " + name + ";";

    return Map.of("iliSnippet", snippet, "cursorHint", Map.of("line", 1, "col", 2));
  }
}
