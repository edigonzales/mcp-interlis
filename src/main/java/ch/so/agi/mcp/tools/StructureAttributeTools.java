package ch.so.agi.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StructureAttributeTools {

  public enum Collection {
    NONE, LIST_OF, BAG_OF
  }

  /**
   * Helper to emit an attribute line that references a STRUCTURE type.
   * Examples:
   *  - address : Demo.Core.Address;
   *  - addresses : LIST OF Demo.Core.Address;
   *  - kontakt : MANDATORY Demo.Core.Contact;
   */
  @Tool(
      name = "createStructureAttributeLine",
      description = "Create an attribute of STRUCTURE type. " +
                    "Params: name (required), structureFqn (required), " +
                    "mandatory (default false), collection (NONE|LIST_OF|BAG_OF, default NONE)."
  )
  public Map<String, Object> createStructureAttributeLine(
      @ToolParam(description = "Attribute name", required = true) String name,
      @ToolParam(description = "Fully-qualified STRUCTURE name, e.g., 'Demo.Core.Address'", required = true) String structureFqn,
      @ToolParam(description = "MANDATORY flag (default false)") @Nullable Boolean mandatory,
      @ToolParam(description = "Collection kind (NONE|LIST_OF|BAG_OF)") @Nullable Collection collection
  ) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Attribute 'name' is required.");
    }
    if (structureFqn == null || structureFqn.isBlank()) {
      throw new IllegalArgumentException("'structureFqn' is required (e.g., 'Demo.Core.Address').");
    }

    String prefix = Boolean.TRUE.equals(mandatory) ? "MANDATORY " : "";
    Collection col = (collection == null) ? Collection.NONE : collection;
    String colPrefix = switch (col) {
      case NONE -> "";
      case LIST_OF -> "LIST OF ";
      case BAG_OF -> "BAG OF ";
    };

    String line = name.trim() + " : " + prefix + colPrefix + structureFqn.trim() + ";";
    return Map.of(
        "iliSnippet", line,
        "cursorHint", Map.of("line", 0, "col", 0)
    );
  }
}
