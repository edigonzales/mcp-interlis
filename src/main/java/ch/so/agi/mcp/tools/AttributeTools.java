package ch.so.agi.mcp.tools;

import ch.so.agi.mcp.model.*;
import ch.so.agi.mcp.model.AttributeLineV2Request.Collection;
import ch.so.agi.mcp.util.NameValidator;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class AttributeTools {

  /**
   * New, strict version that prevents illegal types like bare "NUMERIC".
   * Input: AttributeLineV2Request (name, mandatory?, collection?, typeSpec oneOf).
   * Output: AttributeLineV2Response with a single ILI line.
   */
  @Tool(
      name = "createAttributeLineV2",
      description = """
        Create a single INTERLIS attribute line with strict typing.
        Use either typeSpec.domainFqn or typeSpec.baseType (TEXT, NUM_RANGE, BOOLEAN, COORD, POLYLINE, SURFACE_SIMPLE).
        Examples:
        - TEXT: {"baseType":{"kind":"TEXT","length":120}}
        - NUM_RANGE: {"baseType":{"kind":"NUM_RANGE","min":0.0,"max":100.0,"unitFqn":"INTERLIS.percent"}}
        - Domain: {"domainFqn":"Demo.Farbe"}
        """
  )
  public AttributeLineV2Response createAttributeLineV2(AttributeLineV2Request req) {
    // ---- basic checks
    if (req.getName() == null || req.getName().isBlank()) {
      throw new IllegalArgumentException("Attribute 'name' is required.");
    }
    
    var nv = NameValidator.ascii(); 
    nv.validateIdent(req.getName(), "Attribute name");

    
    if (req.getTypeSpec() == null) {
      throw new IllegalArgumentException("typeSpec is required.");
    }
    
    var ts = req.getTypeSpec();
    if (ts.getDomainFqn() != null && !ts.getDomainFqn().isBlank()) {
      nv.validateFqn(ts.getDomainFqn(), "Domain FQN");
    }

    req.getTypeSpec().validateOneOf();
    

    // ---- build RHS (type)
    String rhs;
    if (req.getTypeSpec().getDomainFqn() != null && !req.getTypeSpec().getDomainFqn().isBlank()) {
      rhs = req.getTypeSpec().getDomainFqn().trim();
    } else {

        var bt = req.getTypeSpec().getBaseType();
        bt.validate();
        rhs = switch (bt.getKind()) {
          case TEXT -> (bt.getLength() == null) ? "TEXT" : "TEXT*" + bt.getLength();
          case MTEXT -> (bt.getLength() == null) ? "MTEXT" : "MTEXT*" + bt.getLength();
          case NUM_RANGE -> {
            String unitPart = (bt.getUnitFqn() != null && !bt.getUnitFqn().isBlank())
                ? " [" + bt.getUnitFqn().trim() + "]" : "";
            yield bt.getMin() + " .. " + bt.getMax() + unitPart;
          }
          case BOOLEAN -> "BOOLEAN";
          case COORD -> "COORD";
          case POLYLINE -> "POLYLINE";
          case SURFACE_SIMPLE -> "SURFACE WITH (STRAIGHTS) VERTEX COORD";
        };
    }

    // ---- prefix (mandatory + collection)
    String prefix = Boolean.TRUE.equals(req.getMandatory()) ? "MANDATORY " : "";
    Collection col = (req.getCollection() == null) ? Collection.NONE : req.getCollection();
    String collectionPrefix = switch (col) {
      case NONE -> "";
      case LIST_OF -> "LIST OF ";
      case BAG_OF -> "BAG OF ";
    };

    String line = req.getName().trim() + " : " + prefix + collectionPrefix + rhs + ";";
    return new AttributeLineV2Response(line);
  }

  /**
   * Optional: keep the old tool name as a strict adapter that FAILS on illegal strings.
   * If you must keep legacy shape {name, type, ...}, you can parse and forward.
   * Here we recommend returning a helpful error instead of guessing.
   */
  @Tool(
      name = "createAttributeLine",
      description = "Deprecated legacy tool. Please use createAttributeLineV2. " +
                    "This legacy endpoint rejects bare NUMERIC and unknown types."
  )
  public AttributeLineV2Response createAttributeLineLegacy(String name, String type) {
    throw new IllegalArgumentException(
        "Deprecated: use createAttributeLineV2 with a 'typeSpec'. " +
        "Bare 'NUMERIC' is not valid in INTERLIS; use baseType.NUM_RANGE with min/max or a domainFqn.");
  }
}
