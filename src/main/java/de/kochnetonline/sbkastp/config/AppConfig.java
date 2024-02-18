package de.kochnetonline.sbkastp.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the application
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppConfig {

    @NotBlank
    private String kafkaBootstrapServer;

    @NotBlank
    private String subscriptionTopic;

    @NotBlank
    private String winTopic;

    @NotBlank
    private String winNotificationTopic;

    @NotBlank
    private String consumerGroup;
}
