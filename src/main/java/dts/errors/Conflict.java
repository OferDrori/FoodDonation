package dts.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class Conflict extends BaseErrorClass{
	
	private static final long serialVersionUID = 1L;

	public Conflict(String itemNotFoundMsg) {
		super(itemNotFoundMsg);
	}

	public Conflict(Throwable cause) {
		super(cause);
	}

	public Conflict(String message, Throwable cause) {
		super(message, cause);
	}
}
