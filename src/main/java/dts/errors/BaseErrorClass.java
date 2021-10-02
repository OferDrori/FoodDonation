package dts.errors;


public class BaseErrorClass extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	
	public BaseErrorClass(String message) {
		super(message);
	}

	public BaseErrorClass(Throwable cause) {
		super(cause);
	}

	public BaseErrorClass(String message, Throwable cause) {
		super(message, cause);
	}
}
