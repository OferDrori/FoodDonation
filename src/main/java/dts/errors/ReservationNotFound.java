package dts.errors;

public class ReservationNotFound extends ResourceNotFound {
	
private static final long serialVersionUID = 1L;
	
	public ReservationNotFound(String reservationNotFoundMsg) {
		super(reservationNotFoundMsg);
	}

	public ReservationNotFound(Throwable cause) {
		super(cause);
	}

	public ReservationNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}
