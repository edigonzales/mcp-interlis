package ch.so.agi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * oneOf:
 *  - {"domainFqn": "..."}
 *  - {"baseType": {...}}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeSpec {
  private String domainFqn;   // option A
  private BaseType baseType;  // option B

  public String getDomainFqn() { return domainFqn; }
  public void setDomainFqn(String domainFqn) { this.domainFqn = domainFqn; }

  public BaseType getBaseType() { return baseType; }
  public void setBaseType(BaseType baseType) { this.baseType = baseType; }

  /** Validate oneOf semantics */
  public void validateOneOf() {
    boolean hasDomain = domainFqn != null && !domainFqn.isBlank();
    boolean hasBase   = baseType != null;
    if (hasDomain == hasBase) { // both or neither
      throw new IllegalArgumentException(
          "typeSpec must have EITHER 'domainFqn' OR 'baseType'.");
    }
  }
}
