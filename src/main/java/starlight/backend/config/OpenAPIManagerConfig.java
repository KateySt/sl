package starlight.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIManagerConfig {
    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Value("${app.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("java.team.maksym@gmail.com");
        contact.setName("Java-team");
        contact.setUrl("http://18.194.159.42:8082");

//        License mitLicense = new License().name("None").url("None");

        Info info = new Info()
                .title("StarLight Management API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Starlight project.");
//                .license(mitLicense);

//        return new OpenAPI().info(info).servers(List.of(devServer));
        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
