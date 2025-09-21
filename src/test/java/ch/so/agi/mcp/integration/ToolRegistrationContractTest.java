package ch.so.agi.mcp.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import ch.so.agi.mcp.model.AttributeLineV2Response;
import ch.so.agi.mcp.tools.AttributeTools;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
class ToolRegistrationContractTest {

  @Autowired ToolCallbackProvider toolCallbackProvider;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void snippetToolsAreRegisteredWithExpectedSchemasAndBehavior() throws Exception {
    Map<String, ToolCallback> callbacksByName =
        Arrays.stream(toolCallbackProvider.getToolCallbacks())
            .collect(Collectors.toMap(cb -> cb.getToolDefinition().name(), Function.identity()));

    assertThat(callbacksByName).containsKeys("createModelSnippet", "createSnippet");

    ToolCallback createModelSnippet = callbacksByName.get("createModelSnippet");
    ToolDefinition snippetDefinition = createModelSnippet.getToolDefinition();
    JsonNode schema = objectMapper.readTree(snippetDefinition.inputSchema());

    List<String> requiredParams =
        schema.path("required").isMissingNode()
            ? List.of()
            : iterableToList(schema.path("required"));

    assertThat(requiredParams).contains("name");

    JsonNode properties = schema.path("properties");
    assertThat(properties.path("name").path("description").asText())
        .isEqualTo("Modellname (Bezeichner ohne Leerzeichen)");
    assertThat(properties.path("lang").path("description").asText())
        .isEqualTo("Sprachcode, z. B. 'de' oder 'en'");
    assertThat(properties.path("uri").path("description").asText())
        .isEqualTo("URI des Modells");
    assertThat(properties.path("version").path("description").asText())
        .isEqualTo("Version im Format YYYY-MM-DD");
    assertThat(properties.path("imports").path("description").asText())
        .isEqualTo("Zusätzliche Imports (z. B. 'GeometryCHLV95_V1')");

    ToolCallback aliasSnippet = callbacksByName.get("createSnippet");
    ToolDefinition aliasDefinition = aliasSnippet.getToolDefinition();
    JsonNode aliasSchema = objectMapper.readTree(aliasDefinition.inputSchema());
    List<String> aliasRequired =
        aliasSchema.path("required").isMissingNode()
            ? List.of()
            : iterableToList(aliasSchema.path("required"));
    assertThat(aliasRequired).contains("name");
    assertThat(aliasSchema.path("properties").has("name")).isTrue();
    assertThat(aliasDefinition.description()).contains("Deprecated alias");

    String requestJson = createSnippetRequest();
    String responseJson = createModelSnippet.call(requestJson);
    JsonNode response = objectMapper.readTree(responseJson);

    assertThat(response.path("iliSnippet").asText())
        .isEqualTo(
            "MODEL TestModel (de) AT \"https://example.org/test\" VERSION \"2024-01-31\" =\n"
                + "  IMPORTS UNQUALIFIED INTERLIS, GeometryCHLV95_V1;\n\n"
                + "END TestModel.\n");
    assertThat(response.path("cursorHint").path("line").asInt()).isEqualTo(2);
    assertThat(response.path("cursorHint").path("col").asInt()).isEqualTo(0);
  }

  private String createSnippetRequest() throws Exception {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("name", "TestModel");
    root.put("lang", "de");
    root.put("uri", "https://example.org/test");
    root.put("version", "2024-01-31");

    ArrayNode imports = root.putArray("imports");
    imports.add("INTERLIS");
    imports.add("GeometryCHLV95_V1");

    return objectMapper.writeValueAsString(root);
  }

  private static List<String> iterableToList(JsonNode arrayNode) {
    return StreamSupport.stream(arrayNode.spliterator(), false)
        .map(JsonNode::asText)
        .collect(Collectors.toList());
  }

  @TestConfiguration
  static class OverrideAttributeToolsConfig {

    @Bean
    @Primary
    AttributeTools attributeTools() {
      return new AttributeTools() {
        @Override
        @Tool(
            name = "createAttributeLineLegacy",
            description =
                "Deprecated legacy tool. Please use createAttributeLineV2. "
                    + "This legacy endpoint rejects bare NUMERIC and unknown types."
        )
        public AttributeLineV2Response createAttributeLineLegacy(String name, String type) {
          return super.createAttributeLineLegacy(name, type);
        }
      };
    }
  }
}
