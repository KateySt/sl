package starlight.backend.email.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record EmailProps(
        String username
) {
}
