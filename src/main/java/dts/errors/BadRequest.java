package dts.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequest extends BaseErrorClass {

	private static final long serialVersionUID = 1L;

	public BadRequest(String badRequestErrMsg) {
		super(badRequestErrMsg);
	}
	
	public BadRequest(Throwable cause) {
		super(cause);
	}

	public BadRequest(String message, Throwable cause) {
		super(message, cause);
	}

}
