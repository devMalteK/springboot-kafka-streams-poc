package de.kochnetonline.sbkastp.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String deliveryTopic;

    @NotBlank
    private String notificationTopic;

    @NotBlank
    private String notificationCountTopic;

    @NotBlank
    private String consumerGroup;

    @NotNull
    private Boolean cleanUpStatesOnStartup;
}
