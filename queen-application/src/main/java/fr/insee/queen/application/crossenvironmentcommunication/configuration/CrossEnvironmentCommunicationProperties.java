package fr.insee.queen.application.crossenvironmentcommunication.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for cross-environment communication using Apache Pulsar.
 * <p>
 * These properties allow enabling or disabling the Pulsar message emitter and consumer
 * independently for cross-environment communication.
 * </p>
 *
 * @param emitter  configuration for true/false the Pulsar message emitter
 * @param consumer configuration for true/false the Pulsar message consumer
 */
@ConfigurationProperties(prefix = "feature.cross-environment-communication")
public record CrossEnvironmentCommunicationProperties(boolean emitter, boolean consumer) {
}
