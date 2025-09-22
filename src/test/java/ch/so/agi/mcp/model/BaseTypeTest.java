package ch.so.agi.mcp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;

class BaseTypeTest {

    @Test
    void validate_allowsTextWithoutLength() {
        BaseType baseType = new BaseType();
        baseType.setKind(BaseType.Kind.TEXT);
        assertDoesNotThrow(baseType::validate);
    }

   // @Disabled
    @Test
    void validate_rejectsTextWithInvalidLength() {
        BaseType baseType = new BaseType();
        baseType.setKind(BaseType.Kind.TEXT);
        baseType.setLength(0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, baseType::validate);
        assertTrue(ex.getMessage().contains("length"));
    }

    @Test
    void validate_numericRangeRequiresBounds() {
        BaseType baseType = new BaseType();
        baseType.setKind(BaseType.Kind.NUM_RANGE);
        baseType.setMin(5.0);
        baseType.setMax(10.0);
        assertDoesNotThrow(baseType::validate);
    }

    @Test
    void validate_numericRangeRejectsInvalidOrder() {
        BaseType baseType = new BaseType();
        baseType.setKind(BaseType.Kind.NUM_RANGE);
        baseType.setMin(10.0);
        baseType.setMax(5.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, baseType::validate);
        assertTrue(ex.getMessage().contains("min < max"));
    }

    @Test
    void validate_requiresKind() {
        BaseType baseType = new BaseType();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, baseType::validate);
        assertTrue(ex.getMessage().contains("baseType.kind"));
    }
}
