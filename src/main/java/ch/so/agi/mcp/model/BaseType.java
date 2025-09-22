package ch.so.agi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseType {

  public enum Kind { TEXT, MTEXT, NUM_RANGE, BOOLEAN, COORD, POLYLINE, SURFACE_SIMPLE }

  private Kind kind;
  private Integer length;
  private Double min;
  private Double max;

  @Pattern(regexp = "^([A-Za-z][A-Za-z0-9_]*)(\\\\.[A-Za-z][A-Za-z0-9_]*)*$", message = "FQN must be dot-separated identifiers")
  private String unitFqn;

  public Kind getKind() { return kind; }
  public void setKind(Kind kind) { this.kind = kind; }

  public Integer getLength() { return length; }
  public void setLength(Integer length) { this.length = length; }

  public Double getMin() { return min; }
  public void setMin(Double min) { this.min = min; }

  public Double getMax() { return max; }
  public void setMax(Double max) { this.max = max; }

  public String getUnitFqn() { return unitFqn; }
  public void setUnitFqn(String unitFqn) { this.unitFqn = unitFqn; }

  public void validate() {
    if (kind == null) throw new IllegalArgumentException("baseType.kind is required.");
    switch (kind) {
      case TEXT, MTEXT -> {
        if (length != null && length < 1) throw new IllegalArgumentException("TEXT requires 'length' >= 1.");
      }
      case NUM_RANGE -> {
        if (min == null || max == null) throw new IllegalArgumentException("NUM_RANGE requires 'min' and 'max'.");
        if (!(min < max)) throw new IllegalArgumentException("NUM_RANGE requires min < max (got " + min + " .. " + max + ").");
      }
      case BOOLEAN, COORD, POLYLINE, SURFACE_SIMPLE -> { /* ok */ }
      default -> throw new IllegalArgumentException("Unsupported baseType.kind: " + kind);
    }
  }
}
