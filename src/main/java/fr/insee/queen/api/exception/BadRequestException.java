package fr.insee.queen.api.exception;

public class BadRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8206024945370390220L;

	private final int code;
    private final String message;

    public BadRequestException(int code, String message) {
        this.code = code;
        this.message = message;
    }

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}
	
}
