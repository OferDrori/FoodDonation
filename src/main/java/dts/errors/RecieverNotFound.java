package dts.errors;

public class RecieverNotFound extends ReservationNotFound{
	
	private static final long serialVersionUID = 1L;
	
	public RecieverNotFound(String itemNotFoundMsg) {
		super(itemNotFoundMsg);
	}

	public RecieverNotFound(Throwable cause) {
		super(cause);
	}

	public RecieverNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}
