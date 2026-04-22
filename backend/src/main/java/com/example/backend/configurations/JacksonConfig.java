package com.example.backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
                @Override
                public String deserialize(JsonParser p, DeserializationContext ctx) {
                    String value = p.getValueAsString();
                    return (value != null) ? value.trim() : null;
                }
            });
            builder.addModules(module);
        };
    }
}