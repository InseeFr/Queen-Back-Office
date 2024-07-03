package fr.insee.queen.jms.model;

public record JMSOutputMessage(
        int code,
        String message
){
    public static JMSOutputMessage createResponse(ResponseCode responseCode) {
        return new JMSOutputMessage(responseCode.getCode(), responseCode.name());
    }

    public static JMSOutputMessage createResponse(ResponseCode responseCode, String messageResponse) {
        return new JMSOutputMessage(responseCode.getCode(), messageResponse);
    }
}
