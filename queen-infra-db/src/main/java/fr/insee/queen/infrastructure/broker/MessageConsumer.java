package fr.insee.queen.infrastructure.broker;

public interface MessageConsumer {
    boolean shouldConsume(String type);
    void consume(String type, BrokerMessage.Payload payload);
}