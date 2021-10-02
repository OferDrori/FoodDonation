package dts.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFound extends BaseErrorClass {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFound(String resourceNotFoundErrorMsg) {
		super(resourceNotFoundErrorMsg);
	}

	public ResourceNotFound(Throwable cause) {
		super(cause);
	}

	public ResourceNotFound(String message, Throwable cause) {
		super(message, cause);
	}

}
