package ch.so.agi.mcp.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelToolsTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-05-01T00:00:00Z"), ZoneOffset.UTC);
    private final ModelTools modelTools = new ModelTools(fixedClock);

    @Test
    @DisplayName("createModelSnippet uses defaults when optional parameters are null or empty")
    void createModelSnippetDefaults() {
        Map<String, Object> result = modelTools.createModelSnippet(
                "TestModel",
                null,
                null,
                null,
                List.of()
        );

        String expectedSnippet = "MODEL TestModel (de) AT \"https://example.org/testmodel\" VERSION \"2024-05-01\" =\n"
                + "  IMPORTS UNQUALIFIED INTERLIS;\n\n"
                + "END TestModel.\n";

        assertEquals(expectedSnippet, result.get("iliSnippet"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> cursorHint = (Map<String, Integer>) result.get("cursorHint");
        assertEquals(Map.of("line", 2, "col", 0), cursorHint);
    }

    @Test
    @DisplayName("createModelSnippet trims provided values before building snippet")
    void createModelSnippetTrimsValues() {
        Map<String, Object> result = modelTools.createModelSnippet(
                "TrimModel",
                " en ",
                " https://data.example/TrimModel ",
                " 2024-01-31 ",
                List.of("INTERLIS", "GeometryCHLV95_V1")
        );

        String expectedSnippet = "MODEL TrimModel (en) AT \"https://data.example/TrimModel\" VERSION \"2024-01-31\" =\n"
                + "  IMPORTS UNQUALIFIED INTERLIS, GeometryCHLV95_V1;\n\n"
                + "END TrimModel.\n";

        assertEquals(expectedSnippet, result.get("iliSnippet"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> cursorHint = (Map<String, Integer>) result.get("cursorHint");
        assertEquals(Map.of("line", 2, "col", 0), cursorHint);
    }

    @Test
    @DisplayName("createModelSnippet validates import identifiers")
    void createModelSnippetValidatesImports() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                modelTools.createModelSnippet(
                        "InvalidImportModel",
                        "de",
                        "https://example.org/invalid",
                        "2024-05-01",
                        List.of("ValidImport", "Invalid-Import")
                )
        );

        assertEquals(
                "Import model name must match [A-Za-z][A-Za-z0-9_]* (starts with a letter, then letters/digits/underscore). Got: 'Invalid-Import'.",
                ex.getMessage()
        );
    }
}
