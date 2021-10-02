package dts.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnAuthorizedError extends BaseErrorClass {
	
	private static final long serialVersionUID = 1L;

	public UnAuthorizedError(String unAuthorizedErrMsg) {
		super(unAuthorizedErrMsg);
	}

	public UnAuthorizedError(Throwable cause) {
		super(cause);
	}

	public UnAuthorizedError(String message, Throwable cause) {
		super(message, cause);
	}
}
