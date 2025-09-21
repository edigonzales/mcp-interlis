package ch.so.agi.mcp.config;

import ch.so.agi.mcp.tools.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolsConfig {

  @Bean
  ToolCallbackProvider iliTools(
      ModelTools modelTools,
      TopicTools topicTools,
      DomainTools domainTools,
      ClassTools classTools,
      AssociationTools assocTools,
      ConstraintTools constraintTools,
      AttributeTools attributeTools,
      StructureTools structureTools,
      StructureAttributeTools structureAttributeTools
  ) {
    return MethodToolCallbackProvider.builder()
        .toolObjects(
            modelTools,
            topicTools,
            domainTools,
            classTools,
            assocTools,
            constraintTools,
            attributeTools,
            structureTools,
            structureAttributeTools
        )
        .build();
  }
}
