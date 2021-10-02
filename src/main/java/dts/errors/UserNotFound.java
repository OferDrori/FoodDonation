package dts.errors;

public class UserNotFound extends ResourceNotFound {

	private static final long serialVersionUID = 1L;

	public UserNotFound(String userNotFoundErrorMsg) {
		super(userNotFoundErrorMsg);
	}

	public UserNotFound(Throwable cause) {
		super(cause);
	}

	public UserNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}
