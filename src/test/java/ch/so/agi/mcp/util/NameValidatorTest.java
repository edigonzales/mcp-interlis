package ch.so.agi.mcp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {

    @Test
    void asciiValidator_acceptsSimpleIdentifier() {
        NameValidator.ascii().validateIdent("Model1", "Model name");
    }

    @Test
    void asciiValidator_rejectsInvalidIdentifier() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> NameValidator.ascii().validateIdent("1Model", "Model name")
        );
        assertTrue(ex.getMessage().contains("Model name"));
    }

    @Test
    void validateFqn_acceptsCompoundName() {
        NameValidator.ascii().validateFqn("Model.Topic.Class", "Class FQN");
    }

    @Test
    void validateFqn_rejectsEmptySegment() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> NameValidator.ascii().validateFqn("Model..Class", "Class FQN")
        );
        assertTrue(ex.getMessage().contains("Class FQN"));
    }
}
