package ch.so.agi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeLineV2Request {

  public enum Collection {
    NONE, LIST_OF, BAG_OF
  }

  private String name;
  private Boolean mandatory;      // default false
  private Collection collection;  // default NONE
  private TypeSpec typeSpec;      // oneOf: domainFqn | baseType

  // getters/setters

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Boolean getMandatory() { return mandatory; }
  public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }

  public Collection getCollection() { return collection; }
  public void setCollection(Collection collection) { this.collection = collection; }

  public TypeSpec getTypeSpec() { return typeSpec; }
  public void setTypeSpec(TypeSpec typeSpec) { this.typeSpec = typeSpec; }
}
