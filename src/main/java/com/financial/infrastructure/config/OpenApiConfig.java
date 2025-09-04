package com.financial.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Financial Real-Time API",
        version = "1.0.0",
        description = "API de Análise Financeira em Tempo Real com indicadores técnicos, cotações e transações",
        contact = @Contact(
            name = "Financial API Team",
            email = "api@financial.com",
            url = "https://financial.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api.financial.com", description = "Production Server")
    }
)
@SecurityScheme(
    name = "apikey",
    type = SecuritySchemeType.APIKEY,
    paramName = "X-API-Key",
    in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
    description = "API Key Authentication. Use 'demo-api-key-12345' for testing."
)
public class OpenApiConfig {
}