package fr.insee.queen.jms.model;

public record JmsResponse(
        int code,
        String message
){
    public static JmsResponse createResponse(ResponseCode responseCode) {
        return new JmsResponse(responseCode.getCode(), responseCode.name());
    }

    public static JmsResponse createResponse(ResponseCode responseCode, String messageResponse) {
        return new JmsResponse(responseCode.getCode(), messageResponse);
    }
}
