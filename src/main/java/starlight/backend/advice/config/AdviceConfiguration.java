package starlight.backend.advice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "advice.config")
public record AdviceConfiguration (
        long delayDays
){
}
