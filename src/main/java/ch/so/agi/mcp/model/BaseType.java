package ch.so.agi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseType {

  public enum Kind {
      TEXT,            // optional length
      MTEXT,           // optional length (multiline)
      NUM_RANGE,       // requires min,max (+ optional unitFqn)    BOOLEAN,         // no extra fields
      BOOLEAN,
      COORD,
      POLYLINE,
      SURFACE_SIMPLE
  }

  private Kind kind;

  // TEXT / MTEXT
  private Integer length;  // null => unbounded
  
  // NUM_RANGE
  private Double min;
  private Double max;
  private String unitFqn;

  // getters/setters
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

  /** Validates required fields per kind */
  public void validate() {
      if (kind == null) {
        throw new IllegalArgumentException("baseType.kind is required.");
      }
      switch (kind) {
        case TEXT, MTEXT -> {
          // length is optional; if present must be >= 1
          if (length != null && length < 1) {
            throw new IllegalArgumentException(kind + " length, if provided, must be >= 1.");
          }
        }
        case NUM_RANGE -> {
          if (min == null || max == null) {
            throw new IllegalArgumentException("NUM_RANGE requires 'min' and 'max'.");
          }
          if (!(min < max)) {
            throw new IllegalArgumentException("NUM_RANGE requires min < max (got " + min + " .. " + max + ").");
          }
        }
        case BOOLEAN, COORD, POLYLINE, SURFACE_SIMPLE -> { /* no extra fields */ }
        default -> throw new IllegalArgumentException("Unsupported baseType.kind: " + kind);
      }
    }
  }
